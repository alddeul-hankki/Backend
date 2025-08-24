package com.alddeul.solsolhanhankki.user.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserApiRequest {
    private String apiKey;
    private String userId;

    public static UserApiRequest of(String apiKey, String userId) {
        return UserApiRequest.builder()
                .apiKey(apiKey)
                .userId(userId)
                .build();
    }
}
