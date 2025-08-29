package com.alddeul.solsolhanhankki.order.application;

import com.alddeul.solsolhanhankki.campus.model.entity.PickupZone;
import com.alddeul.solsolhanhankki.campus.model.repository.PickupZoneRepository;
import com.alddeul.solsolhanhankki.order.application.dto.RestaurantInfo;
import com.alddeul.solsolhanhankki.order.application.port.out.PaymentPort;
import com.alddeul.solsolhanhankki.order.infra.RestaurantClient;
import com.alddeul.solsolhanhankki.order.infra.dto.PaymentRequest;
import com.alddeul.solsolhanhankki.order.model.entity.Groups;
import com.alddeul.solsolhanhankki.order.model.entity.OrderItems;
import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import com.alddeul.solsolhanhankki.order.model.enums.GroupStatus;
import com.alddeul.solsolhanhankki.order.model.repository.GroupRepository;
import com.alddeul.solsolhanhankki.order.model.repository.OrderItemRepository;
import com.alddeul.solsolhanhankki.order.model.repository.OrderRepository;
import com.alddeul.solsolhanhankki.order.presentation.request.CancelRequest;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderItemRequest;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderPreviewRequest;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderRequest;
import com.alddeul.solsolhanhankki.order.presentation.response.OrderConfirmationResponse;
import com.alddeul.solsolhanhankki.order.presentation.response.OrderItemResponse;
import com.alddeul.solsolhanhankki.order.presentation.response.OrderPreviewResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final GroupRepository groupRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PickupZoneRepository pickupZoneRepository;
    private final RestaurantClient restaurantClient;
    private final PaymentPort paymentPort;

    @Value("${payment.callback.url}")
    private String callbackUrl;

    @Transactional(readOnly = true)
    public OrderPreviewResponse getOrderDetailForPreview(OrderPreviewRequest request) {
        OrderCalculationResult result = calculateOrderDetails(
                request.storeId(), request.pickupZoneId(), request.deadlineAt(), request.orderItems()
        );

        return OrderPreviewResponse.builder()
                .storeName(result.restaurantInfo.getStoreName())
                .orderItems(request.orderItems().stream().map(OrderItemResponse::from).collect(Collectors.toList()))
                .menuTotalPrice(result.menuTotalPrice)
                .originalDeliveryFee(result.originalDeliveryFee)
                .expectedDiscountedDeliveryFee(result.expectedDiscountedDeliveryFee)
                .paymentAmount(result.paymentAmount)
                .build();
    }

    @Transactional
    public OrderConfirmationResponse createOrder(OrderRequest request) {

        Long userId = request.userId();

        OrderCalculationResult calcResult = calculateOrderDetails(
                request.storeId(), request.pickupZoneId(), request.deadlineAt(), request.orderItems()
        );

        Orders order = orderRepository.save(Orders.builder()
                .group(calcResult.group)
                .userId(userId)
                .menuTotalPrice(calcResult.menuTotalPrice)
                .initialDeliveryFee(calcResult.deliveryFeeToSend)
                .build());

        List<OrderItems> orderItems = OrderItems.from(order, request.orderItems());
        orderItemRepository.saveAll(orderItems);

        Groups lockedGroup = groupRepository.findByIdWithPessimisticLock(calcResult.group.getId())
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. ID: " + calcResult.group.getId()));
        lockedGroup.addParticipant(calcResult.menuTotalPrice);

        if (lockedGroup.getStatus() == GroupStatus.RECRUITING && lockedGroup.isTriggered()) {
            lockedGroup.trigger(); // 상태를 TRIGGERED로 바꾸고 deadlineAt을 5분 뒤로 변경
        }

        PaymentRequest paymentRequest = PaymentRequest.of(
                order,
                request.storeName(),
                callbackUrl,
                calcResult.paymentAmount
        );

        boolean paymentSuccess = paymentPort.requestPayment(paymentRequest);
        if (!paymentSuccess) {
            throw new RuntimeException("결제 서버 요청에 실패했습니다.");
        }

        return OrderConfirmationResponse.from(order);
    }

    private OrderCalculationResult calculateOrderDetails(String storeId, Long pickupZoneId, OffsetDateTime deadlineAt, List<OrderItemRequest> orderItems) {
        RestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(storeId);

        Groups group = groupRepository.findAvailableGroupBy(storeId, pickupZoneId, deadlineAt)
                .orElseGet(() -> createNewGroupForCalculation(restaurantInfo, pickupZoneId, deadlineAt));

        long menuTotalPrice = calculateMenuTotalPrice(orderItems);
        long originalDeliveryFee = restaurantInfo.calculateFee(menuTotalPrice);
        long expectedDeliveryFee = group.calculateExpectedFee(menuTotalPrice);

        long deliveryFeeToSend;
        if (group.getStatus() == GroupStatus.RECRUITING) {
            deliveryFeeToSend = originalDeliveryFee;
        } else {
            deliveryFeeToSend = expectedDeliveryFee;
        }
        long paymentAmount = menuTotalPrice + deliveryFeeToSend;

        return new OrderCalculationResult(restaurantInfo, group, menuTotalPrice, originalDeliveryFee, expectedDeliveryFee, deliveryFeeToSend, paymentAmount);
    }

    private record OrderCalculationResult(RestaurantInfo restaurantInfo, Groups group, long menuTotalPrice, long originalDeliveryFee, long expectedDiscountedDeliveryFee, long deliveryFeeToSend, long paymentAmount) {}

    private Groups createNewGroupForCalculation(RestaurantInfo restaurantInfo, Long pickupZoneId, OffsetDateTime deadlineAt) {
        PickupZone pickupZone = pickupZoneRepository.findById(pickupZoneId)
                .orElseThrow(() -> new EntityNotFoundException("픽업존을 찾을 수 없습니다."));

        return Groups.builder()
                .pickupZone(pickupZone)
                .storeId(restaurantInfo.getStoreId())
                .storeName(restaurantInfo.getStoreName())
                .minOrderPrice(restaurantInfo.getMinOrderPrice())
                .deliveryFeePolicies(restaurantInfo.getDeliveryFeePolicies())
                .triggerPrice(restaurantInfo.getTriggerPrice())
                .deadlineAt(deadlineAt)
                .pickupAt(deadlineAt.plusHours(1))
                .build();
    }

    private long calculateMenuTotalPrice(List<OrderItemRequest> orderItems) {
        return orderItems.stream()
                .mapToLong(item -> item.pricePerItem() * item.quantity())
                .sum();
    }

    @Transactional
    public void cancelOrder(CancelRequest request) {
        Orders order = orderRepository.findByIdAndUserId(request.orderId(), request.userId())
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없거나 취소 권한이 없습니다."));

        Groups group = order.getGroup();
        if (group.getStatus() != GroupStatus.RECRUITING) {
            throw new IllegalStateException("모집이 마감된 주문은 취소할 수 없습니다.");
        }

         boolean cancelSuccess =  paymentPort.cancelHeldPayment(order.getId());

        if (!cancelSuccess) {
            throw new RuntimeException("취소 요청에 실패했습니다.");
        }

        Groups lockedGroup = groupRepository.findByIdWithPessimisticLock(group.getId())
                .orElseThrow(EntityNotFoundException::new);
        lockedGroup.removeParticipant(order.getMenuTotalPrice());

        order.cancel();

        if (lockedGroup.getParticipantCount() == 0) {
            lockedGroup.cancel();
        }
    }

    @Transactional
    public String processPaymentCallback(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. ID: " + orderId));

        return order.getPaymentToken();
    }

    @Transactional(readOnly = true)
    public OrderConfirmationResponse getOrderByPaymentToken(String paymentToken) {
        Orders order = orderRepository.findByPaymentToken(paymentToken)
                .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 접근입니다."));

        return OrderConfirmationResponse.from(order);
    }
}