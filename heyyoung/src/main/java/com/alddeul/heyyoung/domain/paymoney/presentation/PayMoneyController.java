package com.alddeul.heyyoung.domain.paymoney.presentation;

import com.alddeul.heyyoung.domain.paymoney.application.PayMoneyService;
import com.alddeul.heyyoung.domain.paymoney.presentation.request.PayMoneyRequest;
import com.alddeul.heyyoung.domain.paymoney.presentation.response.PayMoneyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paymoney")
@RequiredArgsConstructor
public class PayMoneyController {
    private final PayMoneyService payMoneyService;

    @PostMapping("/topup")
    public ResponseEntity<PayMoneyResponse> topUpPayMoney(@RequestBody PayMoneyRequest payMoneyRequest) {
        PayMoneyResponse response = payMoneyService.requestTopUpPayMoney(payMoneyRequest);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<PayMoneyResponse> refund(@RequestBody PayMoneyRequest payMoneyRequest) {
        PayMoneyResponse response = payMoneyService.requestRefundPayMoney(payMoneyRequest);
        return ResponseEntity.ok().body(response);
    }
}
