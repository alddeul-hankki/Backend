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

    @GetMapping("/notification/order-success")
    public String testOrderSuccess(@RequestParam("groupId") long groupId) {
    	try {
    		notificationService.createNotificationOrderSuccess(groupId);
    		return "주문 확정 알림 이벤트가 생성되었습니다.";
    	} catch (Exception e) {
    		log.error("주문 확정 알림 생성 실패: {}", e.getMessage());
    		return "생성 실패: " + e.getMessage();
    	}
    }

    @GetMapping("/notification/order-fail")
    public String testOrderFail(@RequestParam("groupId") long groupId) {
    	try {
    		notificationService.createNotificationOrderFail(groupId);
    		return "주문 실패 알림 이벤트가 생성되었습니다.";
    	} catch (Exception e) {
    		log.error("주문 실패 알림 생성 실패: {}", e.getMessage());
    		return "생성 실패: " + e.getMessage();
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