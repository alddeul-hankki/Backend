package com.alddeul.solsolhanhankki.outbox.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alddeul.solsolhanhankki.outbox.model.entity.OutboxEvent;
import com.alddeul.solsolhanhankki.outbox.repository.OutboxEventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public void processUnsentEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByProcessedFalse();

        for (OutboxEvent event : events) {
            try {
                sendEvent(event);

                // 성공 시 processed = true 업데이트
                event.updateProcessed(true);
                outboxEventRepository.save(event);

            } catch (Exception e) {
                // 실패하면 롤백 → processed=false 유지 → 다음 배치에서 재시도
                throw new RuntimeException("이벤트 처리 실패", e);
            }
        }
    }

    private void sendEvent(OutboxEvent event) {
       log.info("발행: {} payload : {}", event.getEventType(), event.getPayload());
    }
}
