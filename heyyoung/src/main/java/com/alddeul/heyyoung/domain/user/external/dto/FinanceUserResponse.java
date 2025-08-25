package com.alddeul.heyyoung.domain.user.external.dto;


import java.time.OffsetDateTime;

public record FinanceUserResponse(
    String userId,
    String userName,
    String institutionCode,
    String userKey,
    OffsetDateTime created,
    OffsetDateTime modified
){}
