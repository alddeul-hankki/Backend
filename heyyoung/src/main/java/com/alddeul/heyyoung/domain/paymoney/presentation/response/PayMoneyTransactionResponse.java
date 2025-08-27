package com.alddeul.heyyoung.domain.paymoney.presentation.response;

public record PayMoneyTransactionResponse(
    Status status,
    String Message
) {
    public static PayMoneyTransactionResponse of(Status status, String Message) { return new  PayMoneyTransactionResponse(status, Message); }
    public enum Status {
        PROCESSING,
        DONE,
    }
}
