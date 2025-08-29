package com.alddeul.heyyoung.domain.paymoney.presentation.request;

public record PayMoneyTransactionRequest(
        String email,
        Long transactionBalance,
        String transactionSummary
) {

}
