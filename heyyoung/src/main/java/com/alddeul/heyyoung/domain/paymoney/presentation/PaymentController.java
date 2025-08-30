package com.alddeul.heyyoung.domain.paymoney.presentation;


import com.alddeul.heyyoung.common.api.response.ApiResponse;
import com.alddeul.heyyoung.domain.paymoney.application.PaymentService;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentRedirectRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PaymentTokenRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PaymentTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping()
    public String redirectPaySystem(@RequestBody PaymentRedirectRequest request) {
//        request = new PaymentRedirectRequest(
//                12L,
//                "테스트 결제",
//                10000L,
//                "http://localhost:8080",
//                1L
//        );
        String redirectUrl = paymentService.redirectPayment(request);
        return redirectUrl;
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
