package com.alddeul.heyyoung.common.outbox;

import com.alddeul.heyyoung.common.outbox.entity.Outbox;
import com.alddeul.heyyoung.domain.paymoney.application.PayMoneyService;
import com.alddeul.heyyoung.domain.paymoney.model.entity.RefundIntent;
import com.alddeul.heyyoung.domain.paymoney.model.entity.TopUpIntent;
import com.alddeul.heyyoung.domain.paymoney.model.repository.RefundIntentRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.TopUpIntentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxWorker {

    private static final int MAX_BURST = 64;
    private static final int LISTEN_TIMEOUT_MS = 200;

    private final OutboxTx tx;
    private final TopUpIntentRepository topUpIntentRepository;
    private final RefundIntentRepository refundIntentRepository;
    private final PayMoneyService payMoneyService;
    private final DataSource dataSource;

    private volatile boolean running = true;
    private Thread listenerThread;

    @PostConstruct
    public void start() {
        listenerThread = new Thread(this::listenLoop, "outbox-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
        log.info("Outbox LISTEN thread started.");
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        log.info("Outbox LISTEN thread stopped.");
    }

    /** 알림이 없어도 타임아웃마다 runUntilEmpty() 실행 (이전 동작) */
    private void listenLoop() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(true);
                PGConnection pgConn = conn.unwrap(PGConnection.class);
                try (Statement st = conn.createStatement()) {
                    st.execute("LISTEN outbox_new");
                }
                log.info("LISTEN outbox_new registered.");

                while (running && !Thread.currentThread().isInterrupted()) {
                    PGNotification[] notes = pgConn.getNotifications(LISTEN_TIMEOUT_MS);
                    // 알림 유무와 관계없이 처리 루틴 실행
                    runUntilEmpty();
                }
            } catch (SQLException e) {
                log.error("LISTEN loop SQL error. Will retry.", e);
                sleepSilently(500);
            } catch (Throwable t) {
                log.error("LISTEN loop fatal error.", t);
                sleepSilently(500);
            }
        }
    }

    private void sleepSilently(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /** 누락 대비: 30초마다 한 번 더 비우기 */
    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    public void safetySweep() {
        runUntilEmpty();
    }

    /** 지금 실행 가능한 잡을 가능한 한 많이 처리 */
    private void runUntilEmpty() {
        int processed = 0;
        while (processed < MAX_BURST) {
            Outbox job = tx.claimOneOrNull();
            if (job == null) break;

            try {
                switch (job.getType()) {
                    case BANK_WITHDRAW -> handleBankWithdraw(job);
                    case BANK_DEPOSIT  -> handleBankDeposit(job);
                }
            } catch (RuntimeException e) {
                log.error("Unexpected error while processing job id={}", job.getId(), e);
                tx.markFailed(job.getId());
                sleepSilently(100);
            }
            processed++;
        }
        if (processed > 0) {
            log.debug("Processed {} outbox jobs ({}).", processed, OffsetDateTime.now());
        }
    }

    /* === TopUp(출금->지갑크레딧) === */
    private void handleBankWithdraw(Outbox job) {
        TopUpIntent intent = topUpIntentRepository.findById(job.getIntentId())
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + job.getIntentId()));

        switch (intent.getStatus()) {
            case CREDIT_CONFIRMED, COMPENSATED, FAILED -> {
                tx.markSucceeded(job.getId());
                return;
            }
            case INIT -> {
                try {
                    payMoneyService.processBankWithdrawal(job.getIntentId());
                    tx.markSucceeded(job.getId());
                    tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_WITHDRAW);
                } catch (RuntimeException e) {
                    log.warn("[WITHDRAW] 은행 출금 실패 intentId={}", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }
            case DEBIT_CONFIRMED -> {
                try {
                    payMoneyService.confirmWithdrawAndCredit(job.getIntentId());
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.warn("[WITHDRAW] 지갑 크레딧 실패 → 보상 전환 intentId={}", job.getIntentId(), e);
                    try {
                        payMoneyService.markTopUpCompensateRequired(job.getIntentId());
                        tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_WITHDRAW);
                    } finally {
                        tx.markSucceeded(job.getId());
                    }
                }
                return;
            }
            case COMPENSATE_REQUIRED -> {
                try {
                    payMoneyService.compensateWithdrawal(job.getIntentId());
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.error("[WITHDRAW] 보상 실패 intentId={}", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }
        }
    }

    /* === Refund(선차감->은행입금->보상) === */
    private void handleBankDeposit(Outbox job) {
        RefundIntent intent = refundIntentRepository.findById(job.getIntentId())
                .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + job.getIntentId()));

        switch (intent.getStatus()) {
            case FAILED, REFUND_FINISHED -> {
                tx.markSucceeded(job.getId());
                return;
            }
            case INIT -> {
                try {
                    payMoneyService.subtractAmount(job.getIntentId());
                    tx.markSucceeded(job.getId());
                    tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_DEPOSIT);
                } catch (RuntimeException e) {
                    log.error("[DEPOSIT] 선차감 실패 intentId={}", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }
            case PRE_DEBIT -> {
                try {
                    payMoneyService.processRefundDeposit(job.getIntentId());
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.warn("[DEPOSIT] 은행 입금 실패 → 보상 전환 intentId={}", job.getIntentId(), e);
                    try {
                        payMoneyService.markRefundCompensateRequired(job.getIntentId());
                        tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_DEPOSIT);
                    } finally {
                        tx.markSucceeded(job.getId());
                    }
                }
                return;
            }
            case COMPENSATE_REQUIRED -> {
                try {
                    payMoneyService.compensateDebit(job.getIntentId());
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.error("[DEPOSIT] 보상 실패 intentId={}", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }
        }
    }
}
