package com.alddeul.solsolhanhankki.order.presentation.request;

public record OrderItemRequest(
        String menuId,
        String menuName,
        String options,
        Long pricePerItem,
        Integer quantity
) {}