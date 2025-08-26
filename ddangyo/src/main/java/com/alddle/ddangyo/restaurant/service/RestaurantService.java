package com.alddle.ddangyo.restaurant.service;

import com.alddle.ddangyo.restaurant.model.dto.*;
import com.alddle.ddangyo.restaurant.model.entity.Menu;
import com.alddle.ddangyo.restaurant.model.entity.MenuDetail;
import com.alddle.ddangyo.restaurant.model.entity.Restaurant;
import com.alddle.ddangyo.restaurant.model.repository.MenuDetailRepository;
import com.alddle.ddangyo.restaurant.model.repository.MenuRepository;
import com.alddle.ddangyo.restaurant.model.repository.RestaurantInfoRepository;
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
    private final RestaurantInfoRepository restaurantInfoRepository;
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

    // 메뉴 상세 조회 (DB에서 조회) - 수정된 부분
    public Optional<MenuDetailResponse> findMenuDetail(String storeId, String menuId) {
        return menuDetailRepository.findByPatstoNoAndMenuId(storeId, menuId)
                .map(this::convertToMenuDetailResponse); // 매핑 메서드 호출
    }

    // 엔티티를 DTO로 변환하는 private 메서드
    private MenuDetailResponse convertToMenuDetailResponse(MenuDetail entity) {
        MenuDetail.Result entityResult = entity.getResult();

        // 1. MenuInfo 변환
        MenuDetail.MenuInfo entityMenuInfo = entityResult.getMenuInfo();
        MenuDetailResponse.MenuInfo dtoMenuInfo = new MenuDetailResponse.MenuInfo(
                entityMenuInfo.getName(),
                entityMenuInfo.getMenuCmpsCont(),
                null // 이미지 필드는 현재 엔티티에 없으므로 null 처리
        );

        // 2. OptionGroup 리스트 변환
        List<MenuDetailResponse.OptionGroup> dtoOptnGrpList = entityResult.getOptnGrpList().stream()
                .map(group -> new MenuDetailResponse.OptionGroup(
                        group.getOptnGrpId(),
                        group.getOptnGrpNm(),
                        group.getEssSlctYn(),
                        group.getMinSlctCnt(),
                        group.getMaxSlctCnt()
                ))
                .collect(Collectors.toList());

        // 3. OptionItem 리스트 변환
        List<MenuDetailResponse.OptionItem> dtoOptnList = entityResult.getOptnList().stream()
                .map(item -> new MenuDetailResponse.OptionItem(
                        item.getOptnGrpId(),
                        item.getOptnId(),
                        item.getOptnNm(),
                        item.getOptnUnitprc()
                ))
                .collect(Collectors.toList());

        // 4. 최종 DTO 조립
        MenuDetailResponse.Result dtoResult = new MenuDetailResponse.Result(dtoMenuInfo, dtoOptnGrpList, dtoOptnList);
        return new MenuDetailResponse(dtoResult);
    }

    public Optional<RestaurantInfoResponse> getRestaurantInfo(RestaurantInfoRequest request) {
        String patstoNo = request.dmaShopSearch().patstoNo();
        return restaurantInfoRepository.findByPatstoNo(patstoNo)
                .map(RestaurantInfoResponse::new); // 조회된 엔티티를 DTO 생성자로 넘겨 변환
    }
}