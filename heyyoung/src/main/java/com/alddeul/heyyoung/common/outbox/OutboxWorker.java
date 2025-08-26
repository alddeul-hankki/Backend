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
        Outbox job = tx.claimOneOrNull();
        if (job == null) return;

        try {
            switch (job.getType()) {
                case BANK_WITHDRAW -> handleBankWithdraw(job);
                case BANK_DEPOSIT -> handleBankDeposit(job);
            }
        } catch (RuntimeException e) {
            tx.markSucceeded(job.getId());
        }
    }

    private void handleBankWithdraw(Outbox job) {
        TopUpIntent intent = topUpIntentRepository.findById(job.getIntentId())
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + job.getIntentId()));


        switch (intent.getStatus()) {
            // 이미 완료된 입금 작업
            case CREDITED -> {
                tx.markSucceeded(job.getId());
                return;
            }

            // 계좌에서 출금이 완료됨. 이후 paymoney에 충전 로직만 필요
            case DEBIT_CONFIRMED ->  {
                payMoneyService.confirmWithdrawAndCredit(job.getIntentId());
                tx.markSucceeded(job.getId());
                return;
            }

            // 보상이 필요 없는 실패. 사용자가 다시 요청을 보내야함.
            case FAILED -> {
                tx.markSucceeded(job.getId());
                return;
            }

            // 첫 시작하는 작업
            case INIT -> {
                // 바로 아래 try문 실행
            }

            default -> {
                log.warn("존재하지 않는 상태입니다.. type={}, jobId={}, status={}",
                        job.getType(), job.getId(), intent.getStatus());

                tx.markSucceeded(job.getId());
                return;
            }
        }
        try {
            payMoneyService.processBankWithdrawal(job.getIntentId());

            payMoneyService.confirmWithdrawAndCredit(job.getIntentId());
            tx.markSucceeded(job.getId());
        } catch (RuntimeException e) {
            log.warn("TopUp withdrawal failed. intentId={}", job.getIntentId(), e);

            intent = topUpIntentRepository.findById(job.getIntentId())
                    .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + job.getIntentId()));
            intent.markFailed();
            topUpIntentRepository.save(intent);

            tx.markSucceeded(job.getId());
        }
    }

    private void handleBankDeposit(Outbox job) {
        RefundIntent intent = refundIntentRepository.findById(job.getIntentId())
                .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + job.getIntentId()));

        switch (intent.getStatus()) {
            // 이미 완료된 refund 작업
            case REFUND_FINISHED -> {
                tx.markSucceeded(job.getId());
                return;
            }

            // 선차감 금액 환불 필요
            case COMPENSATE_REQUIRED -> {
                payMoneyService.compensateDebit(job.getIntentId());
                tx.markSucceeded(job.getId());
                return;
            }

            // 은행 차감 성공, 이후 내부 최종 확정만 필요
            case REFUND_CONFIRMED -> {
                payMoneyService.confirmDebit(job.getIntentId());
                tx.markSucceeded(job.getId());
                return;
            }

            // 보상이 필요 없는 실패. 사용자가 다시 요청을 보내야함.
            case FAILED -> {
                tx.markSucceeded(job.getId());
                return;
            }

            // 최신 상태 반영하기
            case INIT -> {
                payMoneyService.subtractAmount(job.getIntentId());
                intent = refundIntentRepository.findById(job.getIntentId())
                        .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + job.getIntentId()));
            }
            case PRE_DEBIT -> {
                log.info("Refund PRE_DEBIT: will try deposit and then confirm. intentId={}", job.getIntentId());
                // 바로 아래 try 블록 입금 시도 진행
            }
            default -> {
                log.warn("존재하지 않는 상태입니다.. type={}, jobId={}, status={}",
                        job.getType(), job.getId(), intent.getStatus());
                tx.markSucceeded(job.getId());
                return;
            }
        }
        try {
            // PRE_DEBIT 일 때만 입금 시도
            if (intent.getStatus() == RefundIntent.RefundStatus.PRE_DEBIT) {
                payMoneyService.processRefundDeposit(job.getIntentId());
            }

            RefundIntent after = refundIntentRepository.findById(job.getIntentId())
                    .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + job.getIntentId()));

            switch (after.getStatus()) {
                case REFUND_CONFIRMED -> { payMoneyService.confirmDebit(job.getIntentId()); tx.markSucceeded(job.getId()); }
                case COMPENSATE_REQUIRED -> { payMoneyService.compensateDebit(job.getIntentId()); tx.markSucceeded(job.getId()); }
                default -> { tx.markSucceeded(job.getId()); }
            }
        } catch (RuntimeException e) {
            intent = refundIntentRepository.findById(job.getIntentId())
                    .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + job.getIntentId()));
            intent.markCompensateRequired();
            refundIntentRepository.save(intent);
            payMoneyService.compensateDebit(job.getIntentId());
            tx.markSucceeded(job.getId());
        }
    }
}
