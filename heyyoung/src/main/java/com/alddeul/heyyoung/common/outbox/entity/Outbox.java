package com.alddeul.heyyoung.common.outbox.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 입출금 등 SSAFY API와 요청을 처리하는 Outbox 엔티티
 */
@Entity
@Table(name = "outbox")
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
        DONE,
        FAILED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OutboxStatus status;

    // 시도 횟수
    @Column(nullable = false)
    private Integer attempts;

    @Column(name = "next_run_at", nullable = false)
    private OffsetDateTime nextRunAt;

    public static Outbox ready(Long intentId, OutboxType type) {
        Outbox o = new Outbox();
        o.intentId = intentId;
        o.type = type;
        o.status = OutboxStatus.PENDING;
        o.attempts = 0;
        o.nextRunAt = OffsetDateTime.now();
        return o;
    }

    public void markAttempt() {
        this.attempts++;
    }

    public void markDone() {
        this.status = OutboxStatus.DONE;
    }

    // 재시도
    public void markFailed() {
        this.nextRunAt = OffsetDateTime.now().plusSeconds(Math.min(60, attempts * 2));
        this.status = OutboxStatus.PENDING;
        if (attempts > 5) this.status = OutboxStatus.FAILED;
    }
}
