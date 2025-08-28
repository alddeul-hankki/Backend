package com.alddeul.solsolhanhankki.order.presentation;

import com.alddeul.solsolhanhankki.order.application.OrderService;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderPreviewRequest;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderRequest;
import com.alddeul.solsolhanhankki.order.presentation.response.OrderConfirmationResponse;
import com.alddeul.solsolhanhankki.order.presentation.response.OrderPreviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/preview")
    public ResponseEntity<OrderPreviewResponse> previewOrder(@RequestBody OrderPreviewRequest request) {
        OrderPreviewResponse response = orderService.getOrderDetailForPreview(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderConfirmationResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderConfirmationResponse response = orderService.createOrder(orderRequest);
        return ResponseEntity.created(URI.create("/api/orders/" + response.getOrderId())).body(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId, @RequestParam Long userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

}