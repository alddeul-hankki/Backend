package com.alddle.ddangyo.restaurant.service;

import com.alddle.ddangyo.restaurant.model.dto.RestaurantListRequest;
import com.alddle.ddangyo.restaurant.model.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final MongoTemplate mongoTemplate;

    public List<Restaurant> getFilteredRestaurants(RestaurantListRequest request) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        // 1. 행정동 코드 필터링
        if (request.getAdmtnDongCd() != null && !request.getAdmtnDongCd().isEmpty()) {
            criteria.and("admtnDongCd").is(request.getAdmtnDongCd());
        }

        // 2. 카테고리 필터링
        // "03" 코드를 "치킨"으로 매핑하는 로직
        if (request.getCategoryCd() != null && "03".equals(request.getCategoryCd())) {
            criteria.and("categories").is("치킨");
        }

        // 3. 최소 주문 금액 필터링
        if (request.getMinOrdAmtFilterCd() != null) {
            BigDecimal minOrderAmount = null;
            switch (request.getMinOrdAmtFilterCd()) {
                case "1":
                    minOrderAmount = BigDecimal.valueOf(5000);
                    break;
                case "2":
                    minOrderAmount = BigDecimal.valueOf(10000);
                    break;
                case "3":
                    minOrderAmount = BigDecimal.valueOf(15000);
                    break;
                case "4":
                    minOrderAmount = BigDecimal.valueOf(20000);
                    break;
            }
            if (minOrderAmount != null) {
                // `minMenuPrc` 필드가 `minOrderAmount`보다 작거나 같은(lte) 데이터를 찾음
                criteria.and("minMenuPrc").lte(minOrderAmount);
            }
        }

        // 4. 모든 조건을 쿼리에 추가
        query.addCriteria(criteria);

        // 5. 페이징 처리 (요청된 페이지의 데이터만 가져옴)
        query.skip((long) (request.getPageNo() - 1) * request.getPageSize());
        query.limit(request.getPageSize());

        // 6. MongoDB 쿼리 실행
        return mongoTemplate.find(query, Restaurant.class);
    }
}