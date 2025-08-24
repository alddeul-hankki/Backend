package com.alddeul.solsolhanhankki.global.client.dto;

public record ErrorResponse(
        String responseCode,
        String responseMessage
) {}