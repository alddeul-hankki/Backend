package com.alddeul.heyyoung.domain.paymoney.model.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

// 사용자·금액·상태(HOLD -> CAPTURE -> REFUND)를 추적하는 엔티티 입니다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "payment_intent",
        indexes = {
                @Index(name = "idx_intent_state", columnList = "state"),
                @Index(name = "idx_intent_user_created", columnList = "user_id, created_at")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_intent_idem", columnNames = "idempotency_key"))
@Check(constraints = "amount >= 0")
public class PaymentIntent extends BaseIdentityEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_intent_user"))
    private SolUser user;

    @Column(name = "amount", nullable = false)
    private long amount;

    private String summary;

    private String accountNumber;

    private String redirectUrl;

    private Long orderId;

    // 상태 머신
    public enum State {
        REQUIRES_HOLD,
        HOLD_SUCCEEDED,
        HOLD_FAILED,
        CAPTURED,
        REFUND_PENDING,
        REFUNDED,
        CANCELED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private State state = State.REQUIRES_HOLD;

    @Column(name = "idempotency_key", nullable = false, length = 128)
    private String idempotencyKey;

    private PaymentIntent(SolUser user, long amount, String summary, String idempotencyKey, String accountNumber, String redirectUrl, Long orderId) {
        this.user = user;
        this.amount = amount;
        this.summary = summary;
        this.idempotencyKey = idempotencyKey;
        this.accountNumber = accountNumber;
        this.redirectUrl = redirectUrl;
        this.orderId = orderId;
    }

    public static PaymentIntent of(SolUser user, long amount, String summary, String idempotencyKey, String accountNumber, String redirectUrl, Long orderId) {
        return  new PaymentIntent(user, amount, summary, idempotencyKey, accountNumber, redirectUrl, orderId);
    }
}