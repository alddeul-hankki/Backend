package com.alddeul.heyyoung.domain.paymoney.presentation.request;

public record PaymentRequest(
        Long orderId,
        Long userId,
        String redirectUrl,
        Long amount,
        String summary
) {
}
