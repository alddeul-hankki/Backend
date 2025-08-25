package com.alddeul.heyyoung.domain.account.presentation.response;

import com.alddeul.heyyoung.domain.account.model.entity.Account;

public record AccountSummaryResponse(
        String bankName,
        String maskedAccountNumber,
        Long accountId
) {
    public static AccountSummaryResponse from(Account account) {
        String maskedAccountNumber;
        if (account.getAccountNumber().length() < 4) {
            maskedAccountNumber = "****";
        } else {
            maskedAccountNumber = "****" + account.getAccountNumber().substring(account.getAccountNumber().length() - 4);
        }

        return new AccountSummaryResponse(
                account.getBankName(),
                maskedAccountNumber,
                account.getId()
        );
    }
}
