package com.alddeul.heyyoung.common.outbox.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * 결제(충전/환불) Outbox 엔티티
 * - SKIP LOCKED 단건 클레임
 * - nextRunAt 기반 재시도
 * - 2초 타임박스 내 100ms 재시도, 초과 시 FAILED
 */
@Entity
@Table(name = "outbox",
        indexes = {
                @Index(name = "idx_outbox_status_next_run", columnList = "status,next_run_at"),
                @Index(name = "idx_outbox_intent_type", columnList = "intent_id,type")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Outbox extends BaseIdentityEntity {

    @Column(name = "intent_id", nullable = false)
    private Long intentId;

    public enum OutboxType {
        BANK_WITHDRAW,
        BANK_DEPOSIT
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OutboxType type;

    public enum OutboxStatus {
        PENDING,
        IN_PROGRESS,
        DONE,
        FAILED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OutboxStatus status;

    @Column(name = "next_run_at", nullable = false)
    private OffsetDateTime nextRunAt;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    /** 새 잡 생성 (즉시 실행) */
    public static Outbox create(Long intentId, OutboxType type) {
        Outbox o = new Outbox();
        o.intentId = intentId;
        o.type = type;
        o.status = OutboxStatus.PENDING;
        o.nextRunAt = OffsetDateTime.now();
        o.attempts = 0;
        return o;
    }

    /** 기존 코드 호환용 팩토리 */
    public static Outbox ready(Long intentId, OutboxType type) {
        return create(intentId, type);
    }

    public void markInProgress() {
        this.status = OutboxStatus.IN_PROGRESS;
    }

    public void markDone() {
        this.status = OutboxStatus.DONE;
        this.nextRunAt = OffsetDateTime.now();
    }

    /** 실패 → 100ms 재시도, 생성 후 2초 초과 시 FAILED */
    public void markFailed() {
        this.attempts += 1;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime createdAt = getCreatedAt() != null ? getCreatedAt() : now;
        boolean over2s = Duration.between(createdAt, now).toMillis() >= 2000;
        if (over2s) {
            this.status = OutboxStatus.FAILED;
            this.nextRunAt = now;
        } else {
            this.status = OutboxStatus.PENDING;
            this.nextRunAt = now.plusNanos(100_000_000L);
        }
    }
}
