package com.alddeul.solsolhanhankki.user.client.dto;


import java.time.OffsetDateTime;

public record UserApiResponse (
    String userId,
    String userName,
    String institutionCode,
    String userKey,
    OffsetDateTime created,
    OffsetDateTime modified
){}
