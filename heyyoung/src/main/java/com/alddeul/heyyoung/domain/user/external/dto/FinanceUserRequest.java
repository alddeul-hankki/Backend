package com.alddeul.heyyoung.domain.user.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FinanceUserRequest {
    private String apiKey;
    private String userId;

    public static FinanceUserRequest of(String apiKey, String userId) {
        return FinanceUserRequest.builder()
                .apiKey(apiKey)
                .userId(userId)
                .build();
    }
}
