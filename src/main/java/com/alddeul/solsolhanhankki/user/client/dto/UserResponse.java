package com.alddeul.solsolhanhankki.user.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class UserResponse {
    private String userId;
    private String userName;
    private String institutionCode;
    private String userKey;
    private OffsetDateTime  created;
    private OffsetDateTime modified;
}
