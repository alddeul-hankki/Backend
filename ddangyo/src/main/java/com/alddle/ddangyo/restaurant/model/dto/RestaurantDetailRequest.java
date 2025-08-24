package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RestaurantDetailRequest(
        @JsonProperty("dma_shop_search") DmaShopSearch dmaShopSearch
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DmaShopSearch(

            // 현재 가능한 patstoNo는 1102602, 1121669, 1139069, 1163656 입니다.
            @JsonProperty("patsto_no") String patstoNo
    ) {}
}