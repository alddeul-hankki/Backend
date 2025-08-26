package com.alddeul.heyyoung.domain.paymoney.presentation.response;

public record PayMoneyResponse(
    Status status,
    String Message
) {
    public enum Status {
        PROCESSING,
        DONE,
    }
}
