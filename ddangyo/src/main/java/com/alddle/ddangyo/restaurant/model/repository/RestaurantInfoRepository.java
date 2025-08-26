// com/alddle/ddangyo/restaurant/model/repository/RestaurantInfoRepository.java
package com.alddle.ddangyo.restaurant.model.repository;

import com.alddle.ddangyo.restaurant.model.entity.RestaurantInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantInfoRepository extends MongoRepository<RestaurantInfo, String> {

    Optional<RestaurantInfo> findByPatstoNo(String patstoNo);
}