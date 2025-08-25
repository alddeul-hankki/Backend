package com.alddeul.heyyoung.domain.account.presentation.response;

import com.alddeul.heyyoung.domain.account.model.entity.Account;

public record AccountResponse(
        String bankName,
        String bankCode,
        String accountNumber,
        Long accountId
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getBankName(),
                account.getBankCode(),
                account.getAccountNumber(),
                account.getId()
        );
    }
}
