package com.alddeul.solsolhanhankki.notification.controller;

import java.time.OffsetDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alddeul.solsolhanhankki.notification.domain.NotificationEventType;
import com.alddeul.solsolhanhankki.notification.dto.NotificationEventDto;
import com.alddeul.solsolhanhankki.notification.service.NotificationService;
import com.alddeul.solsolhanhankki.outbox.service.OutboxProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sol/api/test")
@RequiredArgsConstructor
public class TestNotificationController {

    private final NotificationService notificationService;
    private final OutboxProcessor outboxProcessor;

    @GetMapping("/notification")
    public String testNotification(@RequestParam(name="userId", defaultValue = "1") long userId) {
        try {
            // 테스트 알림 이벤트 생성
            NotificationEventDto notification = new NotificationEventDto(
                userId,
                "테스트 알림",
                "테스트 알림입니다.!",
                NotificationEventType.FCM_NOTIFICATION_SEND,
                OffsetDateTime.now()
            );
            
            notificationService.sendNotificationToUser(notification);
            
            log.info("테스트 알림 이벤트 생성 완료: userId={}", userId);
            return "테스트 알림 이벤트가 생성되었습니다. Outbox 처리를 기다리세요.";
            
        } catch (Exception e) {
            log.error("테스트 알림 생성 실패: {}", e.getMessage());
            return "테스트 알림 생성 실패: " + e.getMessage();
        }
    }

    @GetMapping("/outbox/process")
    public String processOutbox() {
        try {
            outboxProcessor.processUnsentEvents();
            return "Outbox 이벤트 처리 완료";
        } catch (Exception e) {
            log.error("Outbox 처리 실패: {}", e.getMessage());
            return "Outbox 처리 실패: " + e.getMessage();
        }
    }
}