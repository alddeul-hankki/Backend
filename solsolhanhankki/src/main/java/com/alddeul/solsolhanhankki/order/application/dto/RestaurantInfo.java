package com.alddeul.solsolhanhankki.order.application.dto;

import com.alddeul.solsolhanhankki.order.model.entity.DeliveryFeePolicy;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class RestaurantInfo {

    private String storeId;
    private String storeName;
    private Long minOrderPrice;
    private List<DeliveryFeePolicy> deliveryFeePolicies;
    private Long triggerPrice;

    /**
     * 특정 주문 금액에 대한 배달비를 계산하는 헬퍼 메서드.
     * @param amount 주문 금액
     * @return 적용될 배달비
     */
    public long calculateFee(long amount) {
        if (this.deliveryFeePolicies == null || this.deliveryFeePolicies.isEmpty()) {
            return 0L;
        }

        return deliveryFeePolicies.stream()
                .sorted(Comparator.comparing(DeliveryFeePolicy::getOrderAmountThreshold).reversed())
                .filter(policy -> amount >= policy.getOrderAmountThreshold())
                .findFirst()
                .map(DeliveryFeePolicy::getFee)
                .orElse(
                        deliveryFeePolicies.stream()
                                .min(Comparator.comparing(DeliveryFeePolicy::getOrderAmountThreshold))
                                .map(DeliveryFeePolicy::getFee).orElse(0L)
                );
    }
}