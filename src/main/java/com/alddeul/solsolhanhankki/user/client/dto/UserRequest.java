package com.alddeul.solsolhanhankki.user.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRequest {
    private String apiKey;
    private String userId;

    public static UserRequest of(String apiKey, String userId) {
        return UserRequest.builder()
                .apiKey(apiKey)
                .userId(userId)
                .build();
    }
}
