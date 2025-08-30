package com.alddeul.heyyoung.domain.paymoney.application;

import com.alddeul.heyyoung.common.api.external.dto.ErrorCode;
import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.common.outbox.entity.Outbox;
import com.alddeul.heyyoung.common.outbox.repository.OutboxRepository;
import com.alddeul.heyyoung.domain.account.external.deposit.FinanceAccountClient;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.DepositAccountResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.WithdrawalAccountResponse;
import com.alddeul.heyyoung.domain.account.model.entity.Account;
import com.alddeul.heyyoung.domain.account.model.repository.AccountRepository;
import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoney;
import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoneyLedger;
import com.alddeul.heyyoung.domain.paymoney.model.entity.RefundIntent;
import com.alddeul.heyyoung.domain.paymoney.model.entity.TopUpIntent;
import com.alddeul.heyyoung.domain.paymoney.model.repository.PayMoneyLedgerRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.PayMoneyRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.RefundIntentRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.TopUpIntentRepository;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyCreateRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyInquiryRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyLedgerRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyTransactionRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyCreateResponse;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyInquiryResponse;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyLedgerResponse;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyTransactionResponse;
import com.alddeul.heyyoung.domain.user.application.UserFacade;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import com.alddeul.heyyoung.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayMoneyService {
    private final FinanceAccountClient accountClient;
    private final PayMoneyRepository payMoneyRepository;
    private final OutboxRepository outboxRepository;
    private final TopUpIntentRepository topUpIntentRepository;
    private final RefundIntentRepository refundIntentRepository;
    private final AccountRepository accountRepository;
    private final PayMoneyLedgerRepository payMoneyLedgerRepository;
    private final UserRepository userRepository;
    private final UserFacade userFacade;

    @Transactional
    public PayMoneyCreateResponse createPayMoney(PayMoneyCreateRequest payMoneyCreateRequest) {
        final String email = payMoneyCreateRequest.email();
        SolUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다: " + email));
        // 2. PayMoney 중복 확인
        if (payMoneyRepository.existsByUser(user)) {
            return PayMoneyCreateResponse.of(
                    PayMoneyCreateResponse.Status.ALREADY_CREATED,
                    "이미 생성된 지갑이 있습니다."
            );
        }

        PayMoney payMoney = PayMoney.create(user);
        payMoneyRepository.save(payMoney);

        return PayMoneyCreateResponse.of(PayMoneyCreateResponse.Status.CREATED, "생성되었습니다.");
    }

    @Transactional
    public PayMoneyInquiryResponse inquiryPayMoney(PayMoneyInquiryRequest payMoneyInquiryRequest) {
        final String email = payMoneyInquiryRequest.email();
        if (email == null || email.isEmpty()) {
            return PayMoneyInquiryResponse.of(PayMoneyInquiryResponse.Status.FAILED, null);
        }

        PayMoney payMoney = payMoneyRepository
                .findByUserEmailForUpdate(email)
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + email));

        Long amount = payMoney.getAmount();

        return PayMoneyInquiryResponse.of(PayMoneyInquiryResponse.Status.SUCCESS, amount);
    }

    @Transactional
    public PayMoneyTransactionResponse requestTopUpPayMoney(PayMoneyTransactionRequest payMoneyTransactionRequest) {
        validateAmount(payMoneyTransactionRequest.transactionBalance());

        final String email = payMoneyTransactionRequest.email();

        final String userKey = userFacade.getUserKey(email);
        final String accountNo = accountRepository.findBySolUser_Email(email)
                .map(Account::getAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 계좌가 없습니다."));
        final Long amount = payMoneyTransactionRequest.transactionBalance();
        final String summary = payMoneyTransactionRequest.transactionSummary().isEmpty()
                ? "PayMoney 충전"
                : payMoneyTransactionRequest.transactionSummary();

        /*
          랜덤한 20자리 생성. SSAFY의 YYYYMMDDHHMMSS 와 다르게 랜덤하게 설정했습니다.
          만약 난수가 겹치면 이미 처리된 요청이라 문제가 생기지만 그 확률이 낮고, 너무 예외케이스라서 고려하지 않았습니다.
         */
        final String instTxnNo = generateInstitutionTxnNo();

        TopUpIntent intent = TopUpIntent.init(email, userKey, accountNo, amount, summary, instTxnNo);
        topUpIntentRepository.save(intent);

        outboxRepository.save(Outbox.ready(intent.getId(), Outbox.OutboxType.BANK_WITHDRAW));

        return PayMoneyTransactionResponse.of(PayMoneyTransactionResponse.Status.PROCESSING, "페이머니에 충전 중입니다.");
    }

    @Transactional
    public PayMoneyTransactionResponse requestRefundPayMoney(PayMoneyTransactionRequest payMoneyTransactionRequest) {
        validateAmount(payMoneyTransactionRequest.transactionBalance());

        final String email = payMoneyTransactionRequest.email();

        final String userKey = userFacade.getUserKey(email);
        final String accountNo = accountRepository.findBySolUser_Email(email)
                .map(Account::getAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 계좌가 없습니다."));
        final Long amount = payMoneyTransactionRequest.transactionBalance();
        final String summary = payMoneyTransactionRequest.transactionSummary().isEmpty()
                ? "PayMoney 환불"
                : payMoneyTransactionRequest.transactionSummary();

        /*
          랜덤한 20자리 생성. SSAFY의 YYYYMMDDHHMMSS 와 다르게 랜덤하게 설정했습니다.
          만약 난수가 겹치면 이미 처리된 요청이라 문제가 생기지만 그 확률이 낮고, 너무 예외케이스라서 고려하지 않았습니다.
         */
        final String instTxnNo = generateInstitutionTxnNo();

        RefundIntent intent = RefundIntent.init(email, userKey, accountNo, amount, summary, instTxnNo);
        refundIntentRepository.save(intent);

        outboxRepository.save(Outbox.ready(intent.getId(), Outbox.OutboxType.BANK_DEPOSIT));

        return PayMoneyTransactionResponse.of(PayMoneyTransactionResponse.Status.PROCESSING, "페이머니 환불 처리 중입니다.");
    }

    public void processBankWithdrawal(Long intentId) {
        TopUpIntent intent = topUpIntentRepository.findById(intentId)
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + intentId));

        if (intent.getStatus() == TopUpIntent.TopUpStatus.DEBIT_CONFIRMED) {
            log.info("Bank withdrawal already confirmed: intentId={}", intent.getId());
            return;
        }

        try {
            FinanceApiResponse<WithdrawalAccountResponse> response = accountClient.withdrawDemandDepositAccount(
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

        // 은행 입금 처리
        try {
            FinanceApiResponse<DepositAccountResponse> response = accountClient.depositDemandDepositAccount(
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

                intent.markRefundFinished(refundTxnNo);
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
    public void compensateWithdrawal(Long intentId) {
        TopUpIntent intent = topUpIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + intentId));
        if (intent.getStatus() != TopUpIntent.TopUpStatus.COMPENSATE_REQUIRED) {
            log.info("보상 조건이 만족되지 못했습니다.: intentId={}", intent.getId());
            return;
        }
        // 은행 입금 처리
        try {
            FinanceApiResponse<DepositAccountResponse> response = accountClient.depositDemandDepositAccount(
                    intent.getUserKey(),
                    intent.getAccountNo(),
                    intent.getAmount(),
                    intent.getSummary(),
                    intent.getInstTxnNo()
            );

            if (response.success() || ErrorCode.H1007.equals(response.error().responseCode())) {
                String instTxnNo = (response.success() && response.data() != null)
                        ? response.data().getHeader().getInstitutionTransactionUniqueNo()
                        : intent.getInstTxnNo();

                intent.markCompensated(instTxnNo);
                topUpIntentRepository.save(intent);
                return;
            }
            log.error("은행으로의 입금 (보상 단계)이 실패했습니다.: intentId={}, error={}",
                    intent.getId(), response.error());
            throw new IllegalStateException("은행 입금 실패: " + response.error());

        } catch (Exception e) {
            log.error("Bank refund exception: intentId={}", intent.getId(), e);
            throw new IllegalStateException("Refund deposit exception", e);
        }
    }

    @Transactional
    public void confirmWithdrawAndCredit(Long intentId) {
        TopUpIntent intent = topUpIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + intentId));

        if (intent.getStatus() == TopUpIntent.TopUpStatus.CREDIT_CONFIRMED) {
            log.info("TopUp already credited: intentId={}, email={}", intent.getId(), intent.getEmail());
            return;
        }

        if (intent.getStatus() != TopUpIntent.TopUpStatus.DEBIT_CONFIRMED) {
            throw new IllegalStateException("Cannot credit: invalid status " + intent.getStatus() + " for intent " + intent.getId());
        }

        PayMoney payMoney = payMoneyRepository
                .findByUserEmailForUpdate(intent.getEmail())
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + intent.getEmail()));

        payMoney.addAmount(intent.getAmount());
        payMoneyRepository.save(payMoney);
        String idem = generateInstitutionTxnNo();;
        PayMoneyLedger ledger = PayMoneyLedger.deposit(
                payMoney,
                intent.getAmount(),
                idem,
                "SSAFY 계좌에서 출금"
        );
        payMoneyLedgerRepository.save(ledger);

        intent.markCredited();
        topUpIntentRepository.save(intent);

        log.info("TopUp credited: intentId={}, email={}, amount={}, newBalance={}",
                intent.getId(), intent.getEmail(), intent.getAmount(), payMoney.getAmount());
    }

    @Transactional
    public void subtractAmount(Long intentId) {
        RefundIntent intent = refundIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + intentId));

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
                .findByUserEmailForUpdate(intent.getEmail())
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + intent.getEmail()));


        payMoney.subtractAmount(intent.getAmount());
        payMoneyRepository.save(payMoney);
        String idem = generateInstitutionTxnNo();;
        PayMoneyLedger ledger = PayMoneyLedger.withdraw(
                payMoney,
                intent.getAmount(),
                idem,
                "SSAFY 계좌로 입금"
        );
        payMoneyLedgerRepository.save(ledger);

        intent.markPreDebited();
        refundIntentRepository.save(intent);
    }

    @Transactional
    public void compensateDebit(Long intentId) {
        RefundIntent intent = refundIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + intentId));
        if (intent.getStatus() != RefundIntent.RefundStatus.COMPENSATE_REQUIRED) {
            log.info("보상 조건이 만족되지 못했습니다.: intentId={}", intent.getId());
            return;
        }

        PayMoney payMoney = payMoneyRepository
                .findByUserEmailForUpdate(intent.getEmail())
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + intent.getEmail()));

        String idem = generateInstitutionTxnNo();;
        if (payMoneyLedgerRepository
                .findByPaymoneyIdAndIdempotencyKey(payMoney.getId(), idem)
                .isPresent()) {
            intent.markCompensatedFinished();
            refundIntentRepository.save(intent);
            return;
        }

        payMoney.addAmount(intent.getAmount());
        payMoneyRepository.save(payMoney);

        PayMoneyLedger ledger = PayMoneyLedger.refund(
                payMoney,
                intent.getAmount(),
                idem,
                "SSAFY 계좌 입금 실패로 인한 재충전"
        );
        payMoneyLedgerRepository.save(ledger);

        intent.markCompensatedFinished();
        refundIntentRepository.save(intent);
    }

    @Transactional
    public void markTopUpCompensateRequired(Long intentId) {
        TopUpIntent intent = topUpIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("TopUpIntent missing: " + intentId));
        intent.markCompensateRequired();
    }

    @Transactional
    public void markRefundCompensateRequired(Long intentId) {
        RefundIntent intent = refundIntentRepository.findByIdForUpdate(intentId)
                .orElseThrow(() -> new IllegalArgumentException("RefundIntent missing: " + intentId));
        intent.markCompensateRequired();
    }

    @Transactional(readOnly = true)
    public List<PayMoneyLedgerResponse> getLedgerHistory(PayMoneyLedgerRequest payMoneyLedgerRequest) {
        final String email = payMoneyLedgerRequest.email();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        payMoneyRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalStateException("PayMoney not found for user: " + email));

        return payMoneyLedgerRepository
                .findAllByPaymoney_User_EmailOrderByCreatedAtDesc(email)
                .stream()
                .map(PayMoneyLedgerResponse::toResponse)
                .toList();
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