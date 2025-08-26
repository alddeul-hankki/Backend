package com.alddeul.solsolhanhankki.notification.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alddeul.solsolhanhankki.notification.domain.FcmToken;
import com.alddeul.solsolhanhankki.notification.dto.NotificationEventDto;
import com.alddeul.solsolhanhankki.notification.repository.FcmTokenRepository;
import com.alddeul.solsolhanhankki.outbox.domain.OutboxEvent;
import com.alddeul.solsolhanhankki.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final FcmTokenRepository fcmTokenRepository;
	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper mapper;
	
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
	
	private void createNotificationOutboxEvent(String token, NotificationEventDto notification) {
		Map<String, Object> payloadMap = Map.of(
				"token", token,
				"title", notification.title(),
				"body", notification.body(),
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
