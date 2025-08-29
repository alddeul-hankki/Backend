// OrderRequest.java
package com.alddeul.solsolhanhankki.order.presentation.request;

import com.alddeul.solsolhanhankki.order.presentation.request.OrderItemRequest;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderRequest(
        Long userId,
        String storeId,
        String storeName,
        Long minOrderPrice,
        Long deliveryFee,
        Long pickupZoneId,
        OffsetDateTime deadlineAt,
        OffsetDateTime pickupAt,
        List<OrderItemRequest> orderItems
) {}