package com.alddeul.heyyoung.domain.paymoney.presentation;


import com.alddeul.heyyoung.common.api.response.ApiResponse;
import com.alddeul.heyyoung.domain.paymoney.application.PaymentService;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentRedirectRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentTokenRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PaymentTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping()
    public ResponseEntity<?> redirectPaySystem(@RequestBody PaymentRedirectRequest request) {
        String redirectUrl = paymentService.redirectPayment(request);
        return ResponseEntity.status(HttpStatus.FOUND)  // 302
                .location(URI.create(redirectUrl))
                .build();
    }

    @PostMapping("/token")
    public ApiResponse<PaymentTokenResponse> paymentToken(@RequestBody PaymentTokenRequest request) {
        var response = paymentService.processPaymentToken(request.token(), request.userId());
        return ApiResponse.ok(response);
    }

    @PostMapping("/withdraw")
    public ApiResponse<String> payment(@RequestBody PaymentRequest request) {
        String redirectUrl = paymentService.capturePayment(request);
        return ApiResponse.ok(redirectUrl);
    }

    // 주문 취소
    @PostMapping("cancel")
    public ApiResponse<?> cancelPayment() {
        return null;
    }

}
