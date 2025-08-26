package com.alddle.ddangyo.restaurant.model.repository;

import com.alddle.ddangyo.restaurant.model.entity.MenuDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MenuDetailRepository extends MongoRepository<MenuDetail, String> {
    Optional<MenuDetail> findByPatstoNoAndMenuId(String patstoNo, String menuId);
}