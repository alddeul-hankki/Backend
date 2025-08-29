package com.alddeul.heyyoung.domain.account.presentation.request;

public record TransactionHistoryRequest(
        String email,
        Long accountId,
        Integer range
) {
}
