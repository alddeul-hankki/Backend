package com.alddeul.solsolhanhankki.outbox.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alddeul.solsolhanhankki.outbox.domain.OutboxEvent;
import com.alddeul.solsolhanhankki.outbox.handler.EventHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventHandlerManager {

    private final Map<String, EventHandler> handlers;

    public EventHandlerManager(List<EventHandler> eventHandlers) {
        this.handlers = eventHandlers.stream()
                .collect(Collectors.toMap(
                    EventHandler::getEventType,
                    Function.identity()
                ));
    }

    public void handleEvent(OutboxEvent event) {
        String eventType = event.getEventType();
        EventHandler handler = handlers.get(eventType);
        
        if (handler != null) {
            handler.handle(event);
        } else {
            log.warn("핸들러를 찾을 수 없습니다: eventType={}", eventType);
        }
    }
}