package com.alddle.ddangyo.restaurant.controller;

import com.alddle.ddangyo.restaurant.model.dto.*;
import com.alddle.ddangyo.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ddangyo/api")
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
    @PostMapping("/restaurants/menu")
    public ResponseEntity<MenuDetailResponse> getMenuDetail(@RequestBody MenuDetailRequest requestDto) {
        String storeId = requestDto.dmaMenuInfo().patstoNo();
        String menuId = requestDto.dmaMenuInfo().menuId();
        return restaurantService.findMenuDetail(storeId, menuId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/restaurantinfo")
    public ResponseEntity<RestaurantInfoResponse> getRestaurantInfo(@RequestBody RestaurantInfoRequest request) {
        return restaurantService.getRestaurantInfo(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}