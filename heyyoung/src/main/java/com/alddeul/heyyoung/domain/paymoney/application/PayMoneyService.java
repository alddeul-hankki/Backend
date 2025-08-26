package com.alddeul.heyyoung.domain.paymoney.application;

import com.alddeul.heyyoung.common.api.external.dto.ErrorCode;
import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.common.outbox.entity.Outbox;
import com.alddeul.heyyoung.common.outbox.repository.OutboxRepository;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.DepositAccountResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.WithdrawalAccountResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.port.AccountDepositPort;
import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoney;
import com.alddeul.heyyoung.domain.paymoney.model.entity.RefundIntent;
import com.alddeul.heyyoung.domain.paymoney.model.entity.TopUpIntent;
import com.alddeul.heyyoung.domain.paymoney.model.repository.PayMoneyRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.RefundIntentRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.TopUpIntentRepository;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyResponse;
import com.alddeul.heyyoung.domain.user.application.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayMoneyService {
    private final AccountDepositPort accountDepositPort;
    private final PayMoneyRepository payMoneyRepository;
    private final OutboxRepository outboxRepository;
    private final TopUpIntentRepository topUpIntentRepository;
    private final RefundIntentRepository refundIntentRepository;
    private final UserFacade userFacade;

    @Transactional
    public PayMoneyResponse requestTopUpPayMoney(PayMoneyRequest payMoneyRequest) {
        validateAmount(payMoneyRequest.transactionBalance());

        final String email = payMoneyRequest.email();

        final String userKey = userFacade.getUserKey(email);
        final String accountNo = payMoneyRequest.accountNo();
        final Long amount = payMoneyRequest.transactionBalance();
        final String summary = payMoneyRequest.transactionSummary().isEmpty()
                ? "PayMoney 충전"
                : payMoneyRequest.transactionSummary();

        /*
          랜덤한 20자리 생성. SSAFY의 YYYYMMDDHHMMSS 와 다르게 랜덤하게 설정했습니다.
          만약 난수가 겹치면 이미 처리된 요청이라 문제가 생기지만 그 확률이 낮고, 너무 예외케이스라서 고려하지 않았습니다.
         */
        final String instTxnNo = generateInstitutionTxnNo();

        TopUpIntent intent = TopUpIntent.init(email, userKey, accountNo, amount, summary, instTxnNo);
        topUpIntentRepository.save(intent);

        outboxRepository.save(Outbox.ready(intent.getId(), Outbox.OutboxType.BANK_WITHDRAW));

        return new PayMoneyResponse(PayMoneyResponse.Status.PROCESSING, "처리 중입니다.");
    }

    @Transactional
    public PayMoneyResponse requestRefundPayMoney(PayMoneyRequest payMoneyRequest) {
        validateAmount(payMoneyRequest.transactionBalance());

        final String email = payMoneyRequest.email();

        final String userKey = userFacade.getUserKey(email);
        final String accountNo = payMoneyRequest.accountNo();
        final Long amount = payMoneyRequest.transactionBalance();
        final String summary = payMoneyRequest.transactionSummary().isEmpty()
                ? "PayMoney 환불"
                : payMoneyRequest.transactionSummary();

        /*
          랜덤한 20자리 생성. SSAFY의 YYYYMMDDHHMMSS 와 다르게 랜덤하게 설정했습니다.
          만약 난수가 겹치면 이미 처리된 요청이라 문제가 생기지만 그 확률이 낮고, 너무 예외케이스라서 고려하지 않았습니다.
         */
        final String instTxnNo = generateInstitutionTxnNo();

        RefundIntent intent = RefundIntent.init(email, userKey, accountNo, amount, summary, instTxnNo);
        refundIntentRepository.save(intent);

        outboxRepository.save(Outbox.ready(intent.getId(), Outbox.OutboxType.BANK_DEPOSIT));

        return new PayMoneyResponse(PayMoneyResponse.Status.PROCESSING, "처리 중입니다.");
    }

    public void processBankWithdrawal(Long intentId) {
        TopUpIntent intent = topUpIntentRepository.findById(intentId)
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + intentId));

        if (intent.getStatus() == TopUpIntent.TopUpStatus.DEBIT_CONFIRMED) {
            log.info("Bank withdrawal already confirmed: intentId={}", intent.getId());
            return;
        }

        try {
            FinanceApiResponse<WithdrawalAccountResponse> response = accountDepositPort.withdraw(
                    intent.getUserKey(),
                    intent.getAccountNo(),
                    intent.getAmount(),
                    intent.getSummary(),
                    intent.getInstTxnNo()
            );

            /* 에러코드 H1007은 이미 거래된 요청으로 신뢰 */
            if (response.success() || ErrorCode.H1007.equals(response.error().responseCode())) {
                String withdrawTxnNo = (response.success() && response.data() != null)
                        ? response.data().getHeader().getInstitutionTransactionUniqueNo()
                        : intent.getInstTxnNo();

                intent.markDebitConfirmed(withdrawTxnNo);
                topUpIntentRepository.save(intent);
                return;
            }

            log.error("Bank withdrawal failed: intentId={}, error={}",
                    intent.getId(), response.error());
            throw new IllegalStateException("Bank withdrawal failed: " + response.error());

        } catch (Exception e) {
            log.error("Bank withdrawal exception: intentId={}", intent.getId(), e);
            throw new IllegalStateException("Bank withdrawal exception", e);
        }
    }

    public void processRefundDeposit(Long intentId) {
        RefundIntent intent = refundIntentRepository.findById(intentId)
                .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + intentId));

        if (intent.getStatus() != RefundIntent.RefundStatus.PRE_DEBIT) {
            log.info("은행 입금 전 페이머니에서 선차감 상태가 아닙니다. intentId={}", intent.getId());
            return;
        }
        // 은행 입금 처리
        try {
            FinanceApiResponse<DepositAccountResponse> response = accountDepositPort.deposit(
                    intent.getUserKey(),
                    intent.getAccountNo(),
                    intent.getAmount(),
                    intent.getSummary(),
                    intent.getInstTxnNo()
            );

            if (response.success() || ErrorCode.H1007.equals(response.error().responseCode())) {
                String refundTxnNo = (response.success() && response.data() != null)
                        ? response.data().getHeader().getInstitutionTransactionUniqueNo()
                        : intent.getInstTxnNo();

                intent.markRefundConfirmed(refundTxnNo);
                refundIntentRepository.save(intent);
                return;
            }
            log.error("Refund deposit failed: intentId={}, error={}",
                    intent.getId(), response.error());
            throw new IllegalStateException("Refund deposit failed: " + response.error());

        } catch (Exception e) {
            log.error("Bank refund exception: intentId={}", intent.getId(), e);
            throw new IllegalStateException("Refund deposit exception", e);
        }
    }

    @Transactional
    public void confirmWithdrawAndCredit(Long intentId) {
        TopUpIntent intent = topUpIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + intentId));

        if (intent.getStatus() == TopUpIntent.TopUpStatus.CREDITED) {
            log.info("TopUp already credited: intentId={}, email={}", intent.getId(), intent.getEmail());
            return;
        }

        if (intent.getStatus() != TopUpIntent.TopUpStatus.DEBIT_CONFIRMED) {
            throw new IllegalStateException("Cannot credit: invalid status " + intent.getStatus() + " for intent " + intent.getId());
        }

        PayMoney payMoney = payMoneyRepository
                .findByUserEmail(intent.getEmail())
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + intent.getEmail()));

        payMoney.addAmount(intent.getAmount());
        payMoneyRepository.save(payMoney);

        intent.markCredited();
        topUpIntentRepository.save(intent);

        log.info("TopUp credited: intentId={}, email={}, amount={}, newBalance={}",
                intent.getId(), intent.getEmail(), intent.getAmount(), payMoney.getAmount());
    }

    @Transactional
    public void subtractAmount(Long intentId) {
        RefundIntent intent = refundIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("RefundIntentIntent missing: " + intentId));

        // 이미 완료된 refund 작업
        if (intent.getStatus() == RefundIntent.RefundStatus.REFUND_FINISHED) {
            log.info("Refund already debited: intentId={}, email={}", intent.getId(), intent.getEmail());
            return;
        }

        // 이미 지갑에서 뺐을 경우
        if (intent.getStatus() == RefundIntent.RefundStatus.PRE_DEBIT) {
            log.info("Debit already debited: intentId={}, email={}", intent.getId(), intent.getEmail());
            return;
        }

        if (intent.getStatus() != RefundIntent.RefundStatus.INIT) {
            throw new IllegalStateException("Cannot predebit: invalid status " + intent.getStatus() + " for intent " + intent.getId());
        }

        PayMoney payMoney = payMoneyRepository
                .findByUserEmail(intent.getEmail())
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + intent.getEmail()));

        payMoney.subtractAmount(intent.getAmount());
        payMoneyRepository.save(payMoney);

        intent.markPreDebited();
        refundIntentRepository.save(intent);
    }

    @Transactional
    public void compensateDebit(Long intentId) {
        RefundIntent intent = refundIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("RefundIntentIntent missing: " + intentId));
        if (intent.getStatus() != RefundIntent.RefundStatus.COMPENSATE_REQUIRED) {
            log.info("보상 조건이 만족되지 못했습니다.: intentId={}", intent.getId());
            return;
        }

        PayMoney payMoney = payMoneyRepository
                .findByUserEmail(intent.getEmail())
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + intent.getEmail()));

        payMoney.addAmount(intent.getAmount());
        payMoneyRepository.save(payMoney);

        intent.markCompensatedFinished();
        refundIntentRepository.save(intent);
    }

    @Transactional
    public void confirmDebit(Long intentId) {
        RefundIntent intent = refundIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("RefundIntentIntent missing: " + intentId));

        if (intent.getStatus() == RefundIntent.RefundStatus.REFUND_FINISHED) {
            log.info("Refund already debited: intentId={}, email={}", intent.getId(), intent.getEmail());
            return;
        }

        if (intent.getStatus() != RefundIntent.RefundStatus.REFUND_CONFIRMED) {
            throw new IllegalStateException("Cannot credit: invalid status " + intent.getStatus() + " for intent " + intent.getId());
        }

        intent.markDebited();
        refundIntentRepository.save(intent);
    }

    /**
     * 멱등키 생성을 위한 랜덤 난수 생성 함수
     */
    public static String generateInstitutionTxnNo() {
        final BigInteger TEN_20 = new BigInteger("100000000000000000000");
        final BigInteger RANGE_128 = BigInteger.ONE.shiftLeft(128);
        final BigInteger THRESHOLD = RANGE_128.subtract(RANGE_128.mod(TEN_20));

        while (true) {
            UUID u = UUID.randomUUID();
            byte[] bytes = ByteBuffer.allocate(16)
                    .putLong(u.getMostSignificantBits())
                    .putLong(u.getLeastSignificantBits())
                    .array();

            BigInteger x = new BigInteger(1, bytes);
            if (x.compareTo(THRESHOLD) < 0) {
                BigInteger v = x.mod(TEN_20);
                return String.format("%020d", v);
            }
        }
    }

    private void validateAmount(Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("거래 금액은 0보다 커야 합니다.");
        }
    }
}