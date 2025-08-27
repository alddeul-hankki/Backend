package com.alddeul.heyyoung.domain.paymoney.presentation;

import com.alddeul.heyyoung.common.api.response.ApiResponse;
import com.alddeul.heyyoung.domain.paymoney.application.PayMoneyService;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyTransactionRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paymoney")
@RequiredArgsConstructor
public class PayMoneyController {
    private final PayMoneyService payMoneyService;

    @PostMapping("/topup")
    public ApiResponse<PayMoneyTransactionResponse> topUpPayMoney(@RequestBody PayMoneyTransactionRequest payMoneyTransactionRequest) {
        PayMoneyTransactionResponse response = payMoneyService.requestTopUpPayMoney(payMoneyTransactionRequest);
        return ApiResponse.ok(response);
    }

    @PostMapping("/refund")
    public ApiResponse<PayMoneyTransactionResponse> refund(@RequestBody PayMoneyTransactionRequest payMoneyTransactionRequest) {
        PayMoneyTransactionResponse response = payMoneyService.requestRefundPayMoney(payMoneyTransactionRequest);
        return ApiResponse.ok(response);
    }
}
