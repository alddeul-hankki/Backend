package com.alddeul.solsolhanhankki.order.presentation.response;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record OrderConfirmationResponse(
        Long orderId,
        String storeName,
        Long paymentAmount,
        String paymentRedirectUrl,
        String pickupZoneName,
        OffsetDateTime deadlineAt,
        OffsetDateTime pickupAt
) {
}