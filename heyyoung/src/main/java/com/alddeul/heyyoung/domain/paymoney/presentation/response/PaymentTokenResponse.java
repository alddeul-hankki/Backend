package com.alddeul.heyyoung.domain.paymoney.presentation.response;

import com.alddeul.heyyoung.domain.paymoney.model.entity.PaymentIntent;

public record PaymentTokenResponse(
        long amount,
        long balance,
        String summary,
        String redirectUrl,
        String maskedAccountNumber,
        Long orderId
) {
    public static PaymentTokenResponse from(PaymentIntent paymentIntent, long balance) {
        return new PaymentTokenResponse(
                paymentIntent.getAmount(),
                balance,
                paymentIntent.getSummary(),
                paymentIntent.getRedirectUrl(),
                masking(paymentIntent.getAccountNumber()),
                paymentIntent.getOrderId()
        );
    }

    private static String masking(String accountNumber) {
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
