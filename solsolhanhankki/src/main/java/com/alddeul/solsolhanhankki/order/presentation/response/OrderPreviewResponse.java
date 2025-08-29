package com.alddeul.solsolhanhankki.order.presentation.response;

import lombok.Builder;

import java.util.List;

// '주문 확인 페이지' UI에 필요한 모든 정보를 담는 DTO
@Builder
public record OrderPreviewResponse(
        String storeName,
        List<OrderItemResponse> orderItems,
        Long menuTotalPrice,
        Long originalDeliveryFee,
        Long expectedDiscountedDeliveryFee,
        Long paymentAmount
) {

}