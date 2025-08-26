package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MenuDetailRequest(
        @JsonProperty("dma_menu_info") DmaMenuInfo dmaMenuInfo
) {
    public record DmaMenuInfo(
            @JsonProperty("patsto_no") String patstoNo,
            @JsonProperty("menu_id") String menuId
    ) {}
}