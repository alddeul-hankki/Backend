package com.alddeul.solsolhanhankki.order.presentation.response;

import com.alddeul.solsolhanhankki.order.presentation.request.OrderItemRequest;
import lombok.Builder;

@Builder
public record OrderItemResponse(
        String menuName,
        Integer quantity,
        Long pricePerItem,
        String options
) {


    public static OrderItemResponse from(OrderItemRequest item) {
        return OrderItemResponse.builder()
                .menuName(item.menuName())
                .quantity(item.quantity())
                .pricePerItem(item.pricePerItem())
                .options(item.options())
                .build();
    }
}