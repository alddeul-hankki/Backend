package com.alddeul.heyyoung.common.api.external.dto;

public record ErrorResponse(
        ErrorCode responseCode,
        String responseMessage
) {}