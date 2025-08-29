package com.alddeul.heyyoung.domain.paymoney.presentation.response;

public record PayMoneyInquiryResponse(
        Status status,
        Long amount
) {
    public static PayMoneyInquiryResponse of(Status status) { return new PayMoneyInquiryResponse(status, null); }

    public static PayMoneyInquiryResponse of(Status status, Long amount) { return new PayMoneyInquiryResponse(status, amount); }

    public enum Status {
        SUCCESS,
        FAILED
    }
}
