package com.alddle.ddangyo.restaurant.model.repository;

import com.alddle.ddangyo.restaurant.model.entity.HomeMenu;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface HomeMenuRepository extends MongoRepository<HomeMenu, String> {
    Optional<HomeMenu> findByPatstoNo(String patstoNo);
}