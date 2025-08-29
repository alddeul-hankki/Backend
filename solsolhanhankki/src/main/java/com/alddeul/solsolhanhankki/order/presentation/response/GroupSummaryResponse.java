package com.alddeul.solsolhanhankki.order.presentation.response;

import com.alddeul.solsolhanhankki.order.model.entity.Groups;

import java.time.OffsetDateTime;

public record GroupSummaryResponse(
        Long groupId,
        String storeName,
        String storeId,
        String imageUrl,
        String pickupZoneName,
        OffsetDateTime deadlineAt,
        Integer participantCount,
        Long currentTotalPrice,
        Long amountToTarget,
        OffsetDateTime scheduledDeadlineAt,
        OffsetDateTime scheduledPickupAt
) {

    public static GroupSummaryResponse from(Groups group) {
        long amountToTarget = Math.max(0, group.getTriggerPrice() - group.getCurrentTotalPrice());

        return new GroupSummaryResponse(
                group.getId(),
                group.getStoreName(),
                group.getStoreId(),
                "https://dwdwaxgahvp6i.cloudfront.net/shbimg/biz/img/2025/03/30f785a6-5e47-45cb-82f6-aaebbb05c020.jpg",
                group.getPickupZone().getName(),
                group.getDeadlineAt(),
                group.getParticipantCount(),
                group.getCurrentTotalPrice(),
                amountToTarget,
                group.getScheduledDeadlineAt(),
                group.getScheduledPickupAt()
        );
    }
}