package com.alddeul.solsolhanhankki.order.presentation.response;

import com.alddeul.solsolhanhankki.order.presentation.request.OrderItemRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponse {
    private String menuName;
    private Integer quantity;
    private Long pricePerItem;
    private String options;

    public static OrderItemResponse from(OrderItemRequest item) {
        return OrderItemResponse.builder()
                .menuName(item.menuName())
                .quantity(item.quantity())
                .pricePerItem(item.pricePerItem())
                .options(item.options())
                .build();
    }
}