package com.alddeul.heyyoung.domain.paymoney.presentation.request;

public record PayMoneyTransactionRequest(
        String email,
        String accountNo,
        Long transactionBalance,
        String transactionSummary
) {

}
