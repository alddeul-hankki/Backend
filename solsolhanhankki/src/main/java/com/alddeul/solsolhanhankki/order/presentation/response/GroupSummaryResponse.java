package com.alddeul.solsolhanhankki.order.presentation.response;

import com.alddeul.solsolhanhankki.order.model.entity.Groups;

import java.time.OffsetDateTime;

public record GroupSummaryResponse(
        Long groupId,
        String storeName,
        String pickupZoneName,
        OffsetDateTime deadlineAt,
        Integer participantCount,
        Long currentTotalPrice,
        Long amountToTarget
) {

    public static GroupSummaryResponse from(Groups group) {
        long amountToTarget = Math.max(0, group.getTriggerPrice() - group.getCurrentTotalPrice());

        return new GroupSummaryResponse(
                group.getId(),
                group.getStoreName(),
                group.getPickupZone().getName(),
                group.getDeadlineAt(),
                group.getParticipantCount(),
                group.getCurrentTotalPrice(),
                amountToTarget
        );
    }
}