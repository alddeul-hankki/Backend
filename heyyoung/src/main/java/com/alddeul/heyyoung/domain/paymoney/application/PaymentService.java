package com.alddeul.heyyoung.domain.paymoney.application;

import com.alddeul.heyyoung.common.api.external.dto.ErrorCode;
import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.domain.account.application.AccountFacade;
import com.alddeul.heyyoung.domain.account.external.deposit.FinanceAccountClient;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.WithdrawalAccountResponse;
import com.alddeul.heyyoung.domain.account.model.entity.Account;
import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoney;
import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoneyLedger;
import com.alddeul.heyyoung.domain.paymoney.model.entity.PaymentIntent;
import com.alddeul.heyyoung.domain.paymoney.model.repository.PayMoneyLedgerRepository;
import com.alddeul.heyyoung.domain.paymoney.model.repository.PayMoneyRepository;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentRedirectRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PaymentTokenResponse;
import com.alddeul.heyyoung.domain.user.application.UserFacade;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PayMoneyRepository payMoneyRepository;
    private final PayMoneyLedgerRepository payMoneyLedgerRepository;
    private final PaymentIntentService paymentIntentService;
    private final FinanceAccountClient financeAccountClient;
    private final UserFacade userFacade;
    private final AccountFacade accountFacade;

    // 결제 페이지 이동
    public String redirectPayment(PaymentRedirectRequest request) {
        String token = UUID.randomUUID().toString();
        SolUser user = userFacade.getUserById(request.userId());
        String accountId = accountFacade.getPrimaryAccountByUser(request.userId());
        String summary = request.summary();
        String redirectUrl = request.redirectUrl();
        Long orderId = request.orderId();

        PaymentIntent paymentIntent = PaymentIntent.of(user, request.amount(), summary, token, accountId, redirectUrl, orderId);
        paymentIntentService.save(paymentIntent);

        return UriComponentsBuilder
                .fromUriString("http://localhost:4173/payment")
                .queryParam("token", token)
                .build().toUriString();
    }

    // 결제 요청 처리
    @Transactional
    public PaymentTokenResponse processPaymentToken(String token, Long userId) {
        PaymentIntent paymentIntent = paymentIntentService.getPaymentIntent(token);

        // 내 지갑에서 남은 금액 읽어오기
        PayMoney payMoney = payMoneyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자의 쏠쏠한 페이가 존재하지 않습니다."));
        long balance = payMoney.getAmount();

        return PaymentTokenResponse.from(paymentIntent, balance);
    }


    // 결제 진행
    @Transactional
    public String capturePayment(PaymentRequest request) {
        SolUser user = userFacade.getUserById(request.userId());
        String accountNumber = accountFacade.getPrimaryAccountByUser(request.userId());

        // 내 지갑에서 남은 금액 읽어오기
        PayMoney payMoney = payMoneyRepository.findByUserId(request.userId())
                .orElseThrow(() -> new RuntimeException("사용자의 쏠쏠한 페이가 존재하지 않습니다."));

        long totalAmount = request.amount();
        if (payMoney.getAmount() < totalAmount) {
            // 계좌에서 차액 자동이체
            log.info("계좌에서 남은 잔액을 불러옵니다.");
            Account account = accountFacade.getAccountByAccountNumber(accountNumber);
            String instTxnNo = PayMoneyService.generateInstitutionTxnNo();

            long diffAmount = totalAmount - payMoney.getAmount();
            FinanceApiResponse<WithdrawalAccountResponse> response = financeAccountClient.withdrawDemandDepositAccount(
                    user.getAccessKey(),
                    account.getAccountNumber(),
                    diffAmount,
                    request.summary(),
                    instTxnNo
            );
            if (!response.success() && ErrorCode.A1014.equals(response.error().responseCode())) {
                log.info("잔액이 부족하여 결제에 실패했습니다.");
                return UriComponentsBuilder
                        .fromUriString(request.redirectUrl())
                        .queryParam("success", false)
                        .queryParam("orderId", request.orderId())
                        .build().toUriString();
            }
            payMoney.addAmount(diffAmount);
            String idem = PayMoneyService.generateInstitutionTxnNo();;
            PayMoneyLedger ledger = PayMoneyLedger.deposit(
                    payMoney,
                    diffAmount,
                    idem,
                    "SSAFY 계좌 자동충전"
            );
            payMoneyLedgerRepository.save(ledger);
        }

        payMoney.subtractAmount(totalAmount);
        String idem = PayMoneyService.generateInstitutionTxnNo();;
        PayMoneyLedger ledger = PayMoneyLedger.purchase(
                payMoney,
                totalAmount,
                idem,
                "땡겨요 주문 결제"
        );
        payMoneyLedgerRepository.save(ledger);


        return UriComponentsBuilder
                .fromUriString(request.redirectUrl())
                .queryParam("success", true)
                .queryParam("orderId", request.orderId())
                .build().toUriString();
    }
}
