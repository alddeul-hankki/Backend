package com.alddeul.solsolhanhankki.outbox.handler;

import com.alddeul.solsolhanhankki.outbox.domain.OutboxEvent;

public interface EventHandler {
	public void handle(OutboxEvent event);
	public String getEventType();
}
