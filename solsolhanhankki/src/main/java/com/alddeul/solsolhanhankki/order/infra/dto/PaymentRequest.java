package com.alddeul.solsolhanhankki.order.infra.dto;

import com.alddeul.solsolhanhankki.order.model.entity.OrderItems;
import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaymentRequest {
    private Long userId;
    private Long orderId;
    private Long amount;
    private String redirectUrl;
    private String summary;

    // PaymentRequest.java
    public static PaymentRequest of(Orders order, String storeName, String callbackUrl, long paymentAmount) {
        String summary = createSummary(order, storeName);
        return PaymentRequest.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .amount(paymentAmount)
                .redirectUrl(callbackUrl)
                .summary(summary)
                .build();
    }

    private static String createSummary(Orders order, String storeName) {
        List<OrderItems> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            return storeName;
        }

        String firstMenuName = items.getFirst().getMenuName();
        int totalItems = items.size();

        if (totalItems > 1) {
            return String.format("%s %s 외 %d건", storeName, firstMenuName, totalItems - 1);
        } else {
            return String.format("%s %s", storeName, firstMenuName);
        }
    }
}