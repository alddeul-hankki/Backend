package com.alddle.ddangyo.restaurant.model.dto;

import com.alddle.ddangyo.restaurant.model.entity.Menu;
import com.alddle.ddangyo.restaurant.model.entity.Restaurant;
import java.util.List;

public record RestaurantDetailResponse(
        Restaurant restaurant,
        List<Menu.MenuGroup> menuGroups,
        List<Menu.MenuItem> menuList
) {
    public RestaurantDetailResponse(Restaurant restaurant, Menu menu) {
        this(
                restaurant,
                (menu != null) ? menu.getMenuGroups() : null,
                (menu != null) ? menu.getMenuList() : null
        );
    }
}