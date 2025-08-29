package com.alddeul.solsolhanhankki.order.infra.dto;

import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefundRequest {
    private Long orderId; // 환불 대상의 orderId
    private Long amount;      // 환불할 금액
    private String reason;    // 환불 사유

    public static RefundRequest from(Orders order) {
        return RefundRequest.builder()
                .orderId(order.getId())
                .amount(order.getRefundAmount())
                .reason("공동구매 배달비 차액 환불")
                .build();
    }
}