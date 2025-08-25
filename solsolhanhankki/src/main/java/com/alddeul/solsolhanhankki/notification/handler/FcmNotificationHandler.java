package com.alddeul.solsolhanhankki.notification.handler;

import org.springframework.stereotype.Component;

import com.alddeul.solsolhanhankki.outbox.handler.EventHandler;
import com.alddeul.solsolhanhankki.outbox.model.entity.OutboxEvent;
import com.google.firebase.messaging.FirebaseMessaging;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FcmNotificationHandler implements EventHandler {
	private final FirebaseMessaging firebaseMessaging;
	
	@Override
	public void handle(OutboxEvent event) {
		
	}

	@Override
	public String getEventType() {
		return "NOTIFICATION";
	}

}
