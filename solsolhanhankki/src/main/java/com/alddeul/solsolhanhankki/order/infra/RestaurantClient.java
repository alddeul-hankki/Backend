package com.alddeul.solsolhanhankki.order.infra;

import com.alddeul.solsolhanhankki.order.application.dto.RestaurantInfo;
import com.alddeul.solsolhanhankki.order.infra.dto.DdangyoRestaurantResponse;
import com.alddeul.solsolhanhankki.order.model.entity.DeliveryFeePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RestaurantClient {

    private final WebClient webClient;

    public RestaurantClient(WebClient.Builder builder, @Value("${ddangyo.api.url}") String ddangyoApiUrl) {
        this.webClient = builder.baseUrl(ddangyoApiUrl).build();
    }

    public RestaurantInfo getRestaurantInfo(String storeId) {
        Map<String, Object> requestBody = Map.of(
                "dma_shop_search", Map.of("patsto_no", storeId)
        );

        DdangyoRestaurantResponse response = webClient.post()
                .uri("/api/restaurants/detail")
                .bodyValue(requestBody)
                .retrieve() // 응답을 받기 시작
                .bodyToMono(DdangyoRestaurantResponse.class) // 응답 body를 DTO로 변환
                .block(); // 비동기 결과를 동기적으로 기다림

        if (response == null || response.getRestaurant() == null) {
            throw new RuntimeException("가게 정보를 조회할 수 없습니다: " + storeId);
        }

        return convertToRestaurantInfo(response);

    }

    // 외부 시스템 DTO를 우리 시스템 도메인 DTO로 변환
    private RestaurantInfo convertToRestaurantInfo(DdangyoRestaurantResponse response) {
        DdangyoRestaurantResponse.Restaurant restaurant = response.getRestaurant();

        List<DeliveryFeePolicy> policies = restaurant.getDeliveryFees().stream()
                .map(dto -> new DeliveryFeePolicy(dto.getOrderAmountThreshold(), dto.getFee()))
                .collect(Collectors.toList());

        Long minOrderPrice = policies.stream()
                .mapToLong(DeliveryFeePolicy::getOrderAmountThreshold)
                .min().orElse(0L);

        return RestaurantInfo.builder()
                .storeId(restaurant.getId())
                .storeName(restaurant.getName())
                .minOrderPrice(minOrderPrice)
                .deliveryFeePolicies(policies)
                .build();
    }
}