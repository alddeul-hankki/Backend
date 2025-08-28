package com.alddeul.heyyoung.common.outbox;

import com.alddeul.heyyoung.common.outbox.entity.Outbox;
import com.alddeul.heyyoung.domain.paymoney.application.PayMoneyService;
import com.alddeul.heyyoung.domain.paymoney.model.entity.RefundIntent;
import com.alddeul.heyyoung.domain.paymoney.model.entity.TopUpIntent;
import com.alddeul.heyyoung.domain.paymoney.model.repository.RefundIntentRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.TopUpIntentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxWorker {

    private final OutboxTx tx;
    private final TopUpIntentRepository topUpIntentRepository;
    private final RefundIntentRepository refundIntentRepository;
    private final PayMoneyService payMoneyService;

    @Scheduled(fixedDelay = 1000L)
    public void run() {
        int hops = 0;
        long deadlineNanos = System.nanoTime() + 50_000_000L; // 50ms
        while (System.nanoTime() < deadlineNanos && hops < 20) { // 과도 실행 방지
            Outbox job = tx.claimOneOrNull();
            if (job == null) return;

            try {
                switch (job.getType()) {
                    case BANK_WITHDRAW -> handleBankWithdraw(job);
                    case BANK_DEPOSIT -> handleBankDeposit(job);
                }
            } catch (RuntimeException e) {
                tx.markFailed(job.getId());
            }
            hops++;
        }
    }

    private void handleBankWithdraw(Outbox job) {
        TopUpIntent intent = topUpIntentRepository.findById(job.getIntentId())
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + job.getIntentId()));

        switch (intent.getStatus()) {
            // 이미 종료된 상태
            case CREDIT_CONFIRMED, COMPENSATED, FAILED -> {
                tx.markSucceeded(job.getId());
                return;
            }

            // 1단계: 은행 출금 단계
            case INIT -> {
                try {
                    payMoneyService.processBankWithdrawal(job.getIntentId());
                    log.info("은행에서 출금에 성공했습니다.");
                    tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_WITHDRAW);
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.warn("은행 출금에 실패했습니다. intentId={}", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }

            // 2단계: 지갑 충전 단계
            // 만약 intent의 상태 전이가 이루어지지 않고 종료되어도, 다시 지갑 재충전 로직을 거치기 때문에 문제가 안됨.
            case DEBIT_CONFIRMED -> {
                try {
                    log.info("지갑 충전 시도중...");
                    payMoneyService.confirmWithdrawAndCredit(job.getIntentId());
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    try {
                        log.warn("지갑 충전 실패. 보상 로직 시작...");
                        payMoneyService.markTopUpCompensateRequired(job.getIntentId());
                        log.info("markTopUpCompensateRequired 완료");

                        tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_WITHDRAW);
                        log.info("enqueue 완료");
                    } catch (Exception compensationEx) {
                        log.error("보상 로직 실행 중 오류 발생: ", compensationEx);
                        throw compensationEx; // 또는 적절한 처리
                    }
                    tx.markSucceeded(job.getId());
                }
                return;
            }

            // 예외: 보상 단계
            case COMPENSATE_REQUIRED -> {
                try {
                    log.info("보상 단계 실행 중..");
                    payMoneyService.compensateWithdrawal(job.getIntentId());
                    log.info("은행 입금(보상)이 완료되었습니다.");
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.error("보상(재입금) 실패 intentId={}", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }
        }
    }

    private void handleBankDeposit(Outbox job) {
        RefundIntent intent = refundIntentRepository.findById(job.getIntentId())
                .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + job.getIntentId()));

        switch (intent.getStatus()) {
            // 이미 종료된 상태
            case FAILED, REFUND_FINISHED -> {
                tx.markSucceeded(job.getId());
                return;
            }

            // 1단계: 페이머니 금액 선차감
            case INIT -> {
                try {
                    payMoneyService.subtractAmount(job.getIntentId());
                    tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_DEPOSIT);
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.error("페이머니 선 차감에 실패했습니다. intentId={}", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }

            // 2단계: 은행 입금 시도
            case PRE_DEBIT -> {
                try {
                    payMoneyService.processRefundDeposit(job.getIntentId());
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.error("은행 입금에 실패했습니다.  intentId={}", job.getIntentId(), e);
                    payMoneyService.markRefundCompensateRequired(job.getIntentId());
                    tx.enqueue(job.getIntentId(), Outbox.OutboxType.BANK_DEPOSIT);
                    tx.markSucceeded(job.getId());
                }
                return;
            }

            // 예외: 보상 단계 - 선차감 금액 환불
            case COMPENSATE_REQUIRED -> {
                try {
                    log.info("보상 단계 실행 중..");
                    payMoneyService.compensateDebit(job.getIntentId());
                    log.info("페이머니 충전(보상)이 완료되었습니다.");
                    tx.markSucceeded(job.getId());
                } catch (RuntimeException e) {
                    log.error("페이머니 충전(보상)에 실패했습니다. intentId={} ", job.getIntentId(), e);
                    tx.markFailed(job.getId());
                }
                return;
            }
        }
    }
}
