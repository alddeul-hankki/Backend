package com.alddeul.heyyoung.domain.paymoney.presentation;

import com.alddeul.heyyoung.common.api.response.ApiResponse;
import com.alddeul.heyyoung.domain.paymoney.application.PayMoneyService;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyCreateRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyInquiryRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyTransactionRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyCreateResponse;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyInquiryResponse;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/paymoney")
@RequiredArgsConstructor
public class PayMoneyController {
    private final PayMoneyService payMoneyService;

    @PostMapping("/create")
    private ApiResponse<PayMoneyCreateResponse> createPayMoney(@RequestBody PayMoneyCreateRequest payMoneyCreateRequest) {
        PayMoneyCreateResponse response = payMoneyService.createPayMoney(payMoneyCreateRequest);
        return ApiResponse.ok(response);
    }

    @PostMapping("/inquiry")
    public ApiResponse<PayMoneyInquiryResponse> inquirePayMoney(@RequestBody PayMoneyInquiryRequest payMoneyInquiryRequest) {
        PayMoneyInquiryResponse response = payMoneyService.inquiryPayMoney(payMoneyInquiryRequest);
        return ApiResponse.ok(response);
    }

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
