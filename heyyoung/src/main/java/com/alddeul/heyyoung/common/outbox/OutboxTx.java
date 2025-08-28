package com.alddeul.heyyoung.common.outbox;

import com.alddeul.heyyoung.common.outbox.entity.Outbox;
import com.alddeul.heyyoung.common.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class OutboxTx {
    private final OutboxRepository outboxRepository;

    @Transactional
    public Outbox claimOneOrNull() {
        return outboxRepository
                .findFirstByStatusAndNextRunAtLessThanEqualOrderByNextRunAtAscIdAsc(
                        Outbox.OutboxStatus.PENDING, OffsetDateTime.now()
                )
                .map(job -> {
                    job.markAttempt();
                    return job;
                })
                .orElse(null);
    }

    @Transactional
    public void enqueue(Long intentId, Outbox.OutboxType type) {
        Outbox job = Outbox.ready(intentId, type);
        outboxRepository.save(job);
    }

    @Transactional
    public void markSucceeded(Long jobId) {
        Outbox job = outboxRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Outbox missing: " + jobId));
        job.markDone();
    }

    @Transactional
    public void markFailed(Long jobId) {
        Outbox job = outboxRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Outbox missing: " + jobId));
        job.markFailed();
    }
}
