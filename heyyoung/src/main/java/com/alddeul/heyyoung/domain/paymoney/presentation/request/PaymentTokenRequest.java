package com.alddeul.heyyoung.domain.paymoney.presentation.request;

public record PaymentTokenRequest(
        String token,
        Long userId
) {
}
