package com.alddeul.solsolhanhankki.campus.presentation.response;

import java.math.BigDecimal;

import com.alddeul.solsolhanhankki.campus.model.entity.PickupZone;

import lombok.Builder;

@Builder
public record PickupZoneResponse(
        Long id,
        String name,
        String description,
        BigDecimal latitude,
        BigDecimal longitude

) {
    public static PickupZoneResponse from(PickupZone pickupZone) {
        return PickupZoneResponse.builder()
                .id(pickupZone.getId())
                .name(pickupZone.getName())
                .latitude(pickupZone.getLatitude())
                .longitude(pickupZone.getLongitude())
                .description(pickupZone.getDescription())
                .build();
    }
}