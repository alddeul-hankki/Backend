package com.alddeul.solsolhanhankki.notification.service;

import com.alddeul.solsolhanhankki.notification.domain.FcmToken;
import com.alddeul.solsolhanhankki.notification.domain.NotificationEventType;
import com.alddeul.solsolhanhankki.notification.dto.NotificationEventDto;
import com.alddeul.solsolhanhankki.notification.repository.FcmTokenRepository;
import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import com.alddeul.solsolhanhankki.order.model.repository.OrderRepository;
import com.alddeul.solsolhanhankki.outbox.domain.OutboxEvent;
import com.alddeul.solsolhanhankki.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final FcmTokenRepository fcmTokenRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper mapper;
    private final OrderRepository orderRepository;

    @Transactional
    public void sendNotificationToUser(NotificationEventDto notification) {
        try {
            List<FcmToken> tokens = fcmTokenRepository.findByUserId(notification.userId());

            for(FcmToken token : tokens) {
                createNotificationOutboxEvent(token.getToken(), notification);
            }

        }catch(Exception e){
            log.info("outbox 저장 실패 {}", e.getMessage());
        }
    }

    @Transactional
    public void createNotificationOrderSuccess(long groupId) {
        try {
            List<Orders> orders = orderRepository.findAllByGroupId(groupId);

            for (Orders order : orders) {
                long userId = order.getUserId();
                String storeName = order.getGroup().getStoreName();
                String title = storeName + " 주문이 확정되었어요";
                String body = "픽업 시간: " + order.getGroup().getPickupAt();
                String url = "/solsol";

                NotificationEventDto notification = new NotificationEventDto(
                        userId, title, body, url,
                        NotificationEventType.FCM_NOTIFICATION_SEND, OffsetDateTime.now()
                );

                sendNotificationToUser(notification);
            }
        } catch (Exception e) {
            log.info("주문 확정 알림 생성 실패 {}", e.getMessage());
        }
    }

    // 실패 알림만 새 트랜잭션으로 저장 (fully-qualified 사용으로 import 최소화)
    @org.springframework.transaction.annotation.Transactional(
            propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
    )
    public void createNotificationOrderFail(long groupId) {
        try {
            List<Orders> orders = orderRepository.findAllByGroupId(groupId);

            for (Orders order : orders) {
                long userId = order.getUserId();
                String storeName = order.getGroup().getStoreName();
                String title = storeName + " 주문이 실패했어요";
                String body = "주문이 목표 금액에 도달 하지 못했어요.";
                String url = "/solsol";

                NotificationEventDto notification = new NotificationEventDto(
                        userId, title, body, url,
                        NotificationEventType.FCM_NOTIFICATION_SEND, OffsetDateTime.now()
                );

                sendNotificationToUser(notification);
            }
        } catch (Exception e) {
            log.info("주문 실패 알림 생성 실패 {}", e.getMessage());
        }
    }

    @Transactional
    public void createRecommendNotification(List<Long> userIds, String storeName) {
        try {
            String title = "같이 주문하는 건 어떠신가요?";
            String body = storeName + " 에서 같이 주문을 해보세요!";
            String url = "/solsol";

            for(Long userId : userIds) {
                NotificationEventDto notification = new NotificationEventDto(
                        userId, title, body, url,
                        NotificationEventType.FCM_NOTIFICATION_SEND, OffsetDateTime.now()
                );

                sendNotificationToUser(notification);
            }
        }catch(Exception e) {
            log.info("추천 알림 생성 실패 {}", e.getMessage());
        }
    }

    private void createNotificationOutboxEvent(String token, NotificationEventDto notification) {
        Map<String, Object> payloadMap = Map.of(
                "token", token,
                "title", notification.title(),
                "body", notification.body(),
                "url", notification.url(),
                "userId", notification.userId(),
                "timestamp", System.currentTimeMillis()
        );

        JsonNode payload = mapper.valueToTree(payloadMap);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(notification.userId())
                .aggregateType("NOTIFICATION")
                .eventType(notification.type().toString())
                .payload(payload)
                .build();

        outboxEventRepository.save(outboxEvent);
    }
}