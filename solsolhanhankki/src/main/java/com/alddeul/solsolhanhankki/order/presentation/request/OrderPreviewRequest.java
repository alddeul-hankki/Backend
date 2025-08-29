package com.alddeul.solsolhanhankki.order.presentation.request;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderPreviewRequest(
        String storeId,
        Long pickupZoneId,
        OffsetDateTime deadlineAt,
        List<OrderItemRequest> orderItems
) {}