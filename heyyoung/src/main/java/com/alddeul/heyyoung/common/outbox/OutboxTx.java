package com.alddeul.heyyoung.common.outbox;

import com.alddeul.heyyoung.common.outbox.entity.Outbox;
import com.alddeul.heyyoung.common.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxTx {

    private final OutboxRepository outboxRepository;

    /** 새 잡 enqueue (중복 방지 + 저장) */
    @Transactional
    public void enqueue(Long intentId, Outbox.OutboxType type) {
        boolean exists = outboxRepository.existsByIntentIdAndTypeAndStatusIn(
                intentId, type, List.of(Outbox.OutboxStatus.PENDING, Outbox.OutboxStatus.IN_PROGRESS)
        );
        if (exists) {
            log.debug("Outbox already enqueued: intentId={}, type={}", intentId, type);
            return;
        }
        Outbox job = Outbox.create(intentId, type);
        outboxRepository.save(job);
        log.info("Enqueued outbox job: id={}, intentId={}, type={}", job.getId(), intentId, type);
    }

    /** 단건 클레임 (없으면 null) */
    @Transactional
    public Outbox claimOneOrNull() {
        return outboxRepository
                .findFirstByStatusAndNextRunAtLessThanEqualOrderByNextRunAtAscIdAsc(
                        Outbox.OutboxStatus.PENDING, OffsetDateTime.now()
                )
                .map(job -> {
                    job.markInProgress();
                    outboxRepository.save(job);
                    return job;
                })
                .orElse(null);
    }

    @Transactional
    public void markSucceeded(Long jobId) {
        Outbox job = outboxRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Outbox missing: " + jobId));
        job.markDone();
        outboxRepository.save(job);
    }

    @Transactional
    public void markFailed(Long jobId) {
        Outbox job = outboxRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Outbox missing: " + jobId));
        job.markFailed();
        outboxRepository.save(job);
        log.debug("Job failed mark: id={}, status={}, attempts={}, nextRunAt={}",
                jobId, job.getStatus(), job.getAttempts(), job.getNextRunAt());
    }
}
