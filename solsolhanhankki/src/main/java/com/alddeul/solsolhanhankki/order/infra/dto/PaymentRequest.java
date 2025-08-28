package com.alddeul.solsolhanhankki.order.infra.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
    private Long userId;
    private Long orderId;
    private Long amount;
    private PaymentType paymentType; // HOLD 또는 CAPTURE

    public enum PaymentType {
        HOLD,
        CAPTURE
    }
}