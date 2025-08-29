package com.alddeul.solsolhanhankki.order.presentation.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

// '주문 확인 페이지' UI에 필요한 모든 정보를 담는 DTO
@Getter
@Builder
public class OrderPreviewResponse {
    private String storeName;
    private List<OrderItemResponse> orderItems;
    private Long menuTotalPrice;
    private Long originalDeliveryFee;
    private Long expectedDiscountedDeliveryFee;
    private Long paymentAmount;
}