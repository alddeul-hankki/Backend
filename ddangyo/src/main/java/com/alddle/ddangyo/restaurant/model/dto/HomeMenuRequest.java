package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HomeMenuRequest(
        @JsonProperty("dma_shop_search") DmaShopSearch dmaShopSearch
) {
    public record DmaShopSearch(
            @JsonProperty("patsto_no") String patstoNo
    ) {}
}