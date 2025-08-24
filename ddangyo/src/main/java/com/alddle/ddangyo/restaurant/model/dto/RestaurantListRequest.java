package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RestaurantListRequest(
        @JsonProperty("category_cd") String categoryCd,
        @JsonProperty("sort_cd") String sortCd
) {}