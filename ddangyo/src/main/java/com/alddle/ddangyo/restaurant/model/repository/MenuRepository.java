package com.alddle.ddangyo.restaurant.model.repository;

import com.alddle.ddangyo.restaurant.model.entity.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MenuRepository extends MongoRepository<Menu, String> {
    Optional<Menu> findByPatstoNo(String patstoNo);
}