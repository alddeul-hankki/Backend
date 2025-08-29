package com.alddeul.solsolhanhankki.order.presentation.request;

public record CancelRequest(
        Long orderId,
        Long userId
) {
}
