package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RestaurantDetailRequest(
        @JsonProperty("dma_shop_search") DmaShopSearch dmaShopSearch
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DmaShopSearch(
            @JsonProperty("patsto_no") String patstoNo
    ) {
    }
}