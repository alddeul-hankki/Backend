package com.alddeul.heyyoung.domain.paymoney.presentation.request;

public record PayMoneyRequest(
        String email,
        String accountNo,
        Long transactionBalance,
        String transactionSummary
) {

}
