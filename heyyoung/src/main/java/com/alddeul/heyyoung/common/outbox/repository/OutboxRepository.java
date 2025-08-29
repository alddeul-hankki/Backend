package com.alddeul.heyyoung.common.outbox.repository;

import com.alddeul.heyyoung.common.outbox.entity.Outbox;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * value = "-2" 는 Hibernate의 transaction이 lock 되어 있으면 스킵하라는 SKIP LOCKED 입니다.
 */
@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    Optional<Outbox> findFirstByStatusAndNextRunAtLessThanEqualOrderByNextRunAtAscIdAsc(
            Outbox.OutboxStatus status, OffsetDateTime now
    );
}
