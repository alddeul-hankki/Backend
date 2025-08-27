package com.alddeul.heyyoung.domain.account.presentation.request;

public record ConfirmAccountRequest(
        String email,
        Long accountId,
        String code
) {
}
