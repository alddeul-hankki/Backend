package com.alddeul.heyyoung.domain.paymoney.presentation.request;

public record PaymentRedirectRequest(
        Long userId,
        String summary,
        Long amount,
        String redirectUrl,
        Long orderId
) {
}
