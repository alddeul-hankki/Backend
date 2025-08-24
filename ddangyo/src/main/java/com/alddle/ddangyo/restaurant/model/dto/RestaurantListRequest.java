package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// class -> record로 변경하고, 필드를 소괄호 안에 정의합니다.
@JsonIgnoreProperties(ignoreUnknown = true)
public record RestaurantListRequest(
        @JsonProperty("category_cd") String categoryCd,
        @JsonProperty("sort_cd") String sortCd
) {
}