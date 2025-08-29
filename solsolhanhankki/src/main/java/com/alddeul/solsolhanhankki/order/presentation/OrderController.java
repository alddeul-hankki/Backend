package com.alddeul.solsolhanhankki.order.presentation;

import com.alddeul.solsolhanhankki.order.application.OrderService;
import com.alddeul.solsolhanhankki.order.presentation.request.CancelRequest;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderPreviewRequest;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderRequest;
import com.alddeul.solsolhanhankki.order.presentation.response.OrderConfirmationResponse;
import com.alddeul.solsolhanhankki.order.presentation.response.OrderPreviewResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/sol/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Value("${solsol.client.url}")
    private String clientAppUrl;

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

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestBody CancelRequest cancelRequest) {
        orderService.cancelOrder(cancelRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/payment/callback")
    public void handlePaymentCallback(
            @RequestParam("orderId") Long orderId,
            @RequestParam("success") boolean success,
            HttpServletResponse response) throws IOException {

        String paymentToken = orderService.processPaymentCallback(orderId);

        String finalRedirectUrl;

        if (success) {
            finalRedirectUrl = clientAppUrl + "/order/complete?payment_token=" + paymentToken;
        } else {
            finalRedirectUrl = clientAppUrl + "/order/fail?payment_token=" + paymentToken;
        }

        response.sendRedirect(finalRedirectUrl);
    }

    @GetMapping("/result")
    public ResponseEntity<OrderConfirmationResponse> getOrderResultByToken(
            @RequestParam("payment_token") String paymentToken) {

        OrderConfirmationResponse response = orderService.getOrderByPaymentToken(paymentToken);
        return ResponseEntity.ok(response);
    }

}