package com.alddle.ddangyo.restaurant.controller;

import com.alddle.ddangyo.restaurant.model.dto.RestaurantDetailResponse;
import com.alddle.ddangyo.restaurant.model.dto.RestaurantListRequest;
import com.alddle.ddangyo.restaurant.model.dto.RestaurantSummaryResponse;
import com.alddle.ddangyo.restaurant.model.dto.RestaurantDetailRequest;
import com.alddle.ddangyo.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/restaurants")
    public ResponseEntity<List<RestaurantSummaryResponse>> getRestaurants(@RequestBody RestaurantListRequest requestDto) {
        List<RestaurantSummaryResponse> restaurants = restaurantService.findRestaurants(
                requestDto.categoryCd(), requestDto.sortCd());
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping("/restaurants/detail")
    public ResponseEntity<RestaurantDetailResponse> getRestaurantDetail(@RequestBody RestaurantDetailRequest requestDto) {
        String storeId = requestDto.dmaShopSearch().patstoNo();
        return restaurantService.findRestaurantDetail(storeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}