package com.alddeul.heyyoung.domain.paymoney.presentation.response;

public record PayMoneyCreateResponse(
        Status status,
        String Message
) {
    public static PayMoneyCreateResponse of(Status status, String Message) {
        return new PayMoneyCreateResponse(status, Message);
    }

    public enum Status {
        CREATED,
        ALREADY_CREATED,
    }
}
