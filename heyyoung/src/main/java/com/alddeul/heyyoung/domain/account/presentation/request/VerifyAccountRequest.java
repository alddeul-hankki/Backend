package com.alddeul.heyyoung.domain.account.presentation.request;

public record VerifyAccountRequest(
        String email,
        Long accountId
) {
}
