package com.alddle.ddangyo.restaurant.model.dto;

import com.alddle.ddangyo.restaurant.model.entity.Restaurant;

import java.util.List;


public record RestaurantSummaryResponse(
        String id,
        String name,
        List<Restaurant.DeliveryFee> delvFees,
        String delvFeeNm,
        String patstoImageFile,
        int reCnt,
        int goodCnt,
        String delvTm
) {

    public RestaurantSummaryResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDelvFee(),
                restaurant.getDelvFeeNm(),
                restaurant.getPatstoImageFile(),
                restaurant.getReCnt(),
                restaurant.getGoodCnt(),
                restaurant.getDelvTm()
        );
    }
}