// com/alddle/ddangyo/restaurant/model/repository/RestaurantInfoRepository.java
package com.alddle.ddangyo.restaurant.model.repository;

import com.alddle.ddangyo.restaurant.model.entity.RestaurantInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantInfoRepository extends MongoRepository<RestaurantInfo, String> { // ID 타입이 String이라고 가정

    /**
     * 가맹점 번호(patstoNo)로 가게 상세 정보 조회
     * @param patstoNo 가맹점 번호
     * @return Optional<RestaurantInfo>
     */
    Optional<RestaurantInfo> findByPatstoNo(String patstoNo);
}