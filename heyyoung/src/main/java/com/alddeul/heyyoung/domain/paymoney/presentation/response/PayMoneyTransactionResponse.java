package com.alddeul.heyyoung.domain.paymoney.presentation.response;

public record PayMoneyTransactionResponse(
    Status status,
    String Message
) {
    public enum Status {
        PROCESSING,
        DONE,
    }
}
