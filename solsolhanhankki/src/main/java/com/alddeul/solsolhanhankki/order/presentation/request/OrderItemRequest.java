package com.alddeul.solsolhanhankki.order.presentation.request;

import java.util.List;

public record OrderItemRequest(
        String menuId,
        String menuName,
        String options,
        Long pricePerItem,
        Integer quantity,
        List<String> category
) {}