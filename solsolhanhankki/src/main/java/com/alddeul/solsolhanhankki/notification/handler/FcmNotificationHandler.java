package com.alddeul.solsolhanhankki.notification.handler;

import org.springframework.stereotype.Component;

import com.alddeul.solsolhanhankki.outbox.domain.OutboxEvent;
import com.alddeul.solsolhanhankki.outbox.handler.EventHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmNotificationHandler implements EventHandler {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void handle(OutboxEvent event) {
        JsonNode payload = event.getPayload();
        
        log.info("msg : {}", payload.toString());
        
        String token = payload.get("token").asText();
        String title = payload.get("title").asText();
        String body = payload.get("body").asText();

        try {
        	
            String messageId = sendFcmNotification(token, title, body);
            log.info("FCM 알림 발송 성공: title={}, body={}",title, body);
        
        } catch (Exception e) {
        
        	log.error("FCM 알림 발송 실패: {}",e.getMessage());
            throw new RuntimeException("FCM 알림 발송 실패", e);
        
        }
    }
    
    @Override
    public String getEventType() {
        return "FCM_NOTIFICATION_SEND";
    }

    private String sendFcmNotification(String token, String title, String body) 
            throws FirebaseMessagingException {
        
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("clickAction", "OPEN_APP") // 알림 클릭 시 앱 열기
                .setWebpushConfig(
                    com.google.firebase.messaging.WebpushConfig.builder()
                        .setNotification(
                            com.google.firebase.messaging.WebpushNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setRequireInteraction(true) // 사용자가 수동으로 닫을 때까지 유지
                                .build()
                        )
                        
                        .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                        .build()
                )
                .build();

        String response = firebaseMessaging.send(message);
        
        log.debug("FCM 메시지 발송: token={}..., messageId={}", 
                token.substring(0, Math.min(10, token.length())), response);
                
        return response;
    }
}