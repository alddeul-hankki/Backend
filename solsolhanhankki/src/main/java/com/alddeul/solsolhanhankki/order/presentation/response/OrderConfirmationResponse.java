package com.alddeul.solsolhanhankki.order.presentation.response;

import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import lombok.Builder;

@Builder
public record OrderConfirmationResponse(
        Long orderId,
        String storeName,
        Long paymentAmount
) {


    public static OrderConfirmationResponse from(Orders order) {
        return OrderConfirmationResponse.builder()
                .orderId(order.getId())
                .storeName(order.getGroup().getStoreName())
                .paymentAmount(order.getMenuTotalPrice() + order.getInitialDeliveryFee())
                .build();
    }
}