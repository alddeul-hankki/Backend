package com.alddle.ddangyo.restaurant.service;

import com.alddle.ddangyo.restaurant.model.dto.HomeMenuResponse;
import com.alddle.ddangyo.restaurant.model.dto.MenuDetailResponse;
import com.alddle.ddangyo.restaurant.model.dto.RestaurantDetailResponse;
import com.alddle.ddangyo.restaurant.model.dto.RestaurantSummaryResponse;
import com.alddle.ddangyo.restaurant.model.entity.Menu;
import com.alddle.ddangyo.restaurant.model.entity.Restaurant;
import com.alddle.ddangyo.restaurant.model.repository.HomeMenuRepository;
import com.alddle.ddangyo.restaurant.model.repository.MenuDetailRepository;
import com.alddle.ddangyo.restaurant.model.repository.MenuRepository;
import com.alddle.ddangyo.restaurant.model.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final HomeMenuRepository homeMenuRepository;
    private final MenuDetailRepository menuDetailRepository;
    private final ObjectMapper objectMapper;

    public List<RestaurantSummaryResponse> findRestaurants(String categoryCd, String sortCd) {
        Sort sort = switch (sortCd) {
            case "05" -> Sort.by(Sort.Direction.DESC, "goodCnt");
            case "06" -> Sort.by(Sort.Direction.DESC, "reCnt");
            default -> Sort.unsorted();
        };
        String category = switch (categoryCd) {
            case "03" -> "치킨";
            case "04" -> "피자";
            default -> null;
        };

        List<Restaurant> restaurants = (category != null)
                ? restaurantRepository.findByCategoriesContains(category, sort)
                : restaurantRepository.findAll(sort);

        return restaurants.stream()
                .map(RestaurantSummaryResponse::new)
                .collect(Collectors.toList());
    }

    public Optional<RestaurantDetailResponse> findRestaurantDetail(String id) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    Menu menu = menuRepository.findByPatstoNo(id).orElse(null);
                    return new RestaurantDetailResponse(restaurant, menu);
                });

    }

    // 홈 메뉴 조회 (DB에서 조회)
    public Optional<HomeMenuResponse> findHomeMenu(String storeId) {
        return homeMenuRepository.findByPatstoNo(storeId)
                .map(homeMenu -> objectMapper.convertValue(homeMenu, HomeMenuResponse.class));
    }

    // 메뉴 상세 조회 (DB에서 조회)
    public Optional<MenuDetailResponse> findMenuDetail(String storeId, String menuId) {
        return menuDetailRepository.findByPatstoNoAndMenuId(storeId, menuId)
                .map(menuDetail -> objectMapper.convertValue(menuDetail, MenuDetailResponse.class));
    }

}