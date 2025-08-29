package com.alddeul.solsolhanhankki.order.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeliveryFeePolicy {

    private Long orderAmountThreshold; // 이 금액 이상일 때
    private Long fee;                  // 적용될 배달비
}