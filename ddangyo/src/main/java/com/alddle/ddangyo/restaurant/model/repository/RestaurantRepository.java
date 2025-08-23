package com.alddle.ddangyo.restaurant.model.repository;

import com.alddle.ddangyo.restaurant.model.entity.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    // 기본 CRUD 메서드는 자동 제공

    // 행정동 코드로 음식점을 찾는 메서드
    List<Restaurant> findByAdmtnDongCd(String admtnDongCd);

    // 특정 카테고리를 포함하는 음식점을 찾는 메서드
    List<Restaurant> findByCategoriesContains(String category);

    // 행정동 코드와 특정 카테고리를 동시에 만족하는 음식점을 찾는 메서드
    List<Restaurant> findByAdmtnDongCdAndCategoriesContains(String admtnDongCd, String category);
}