package com.alddeul.heyyoung.domain.paymoney.model.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.OffsetDateTime;

// 사용자가 주문 예약 시 hold 정보 엔티티
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "paymoney_hold",
        indexes = {
                @Index(name = "idx_hold_paymoney_state", columnList = "paymoney_id, state"),
                @Index(name = "idx_hold_expires", columnList = "expires_at")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_hold_intent", columnNames = "intent_id"))
@Check(constraints = "amount >= 0")
public class PayMoneyHold extends BaseIdentityEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paymoney_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hold_paymoney"))
    private PayMoney paymoney;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intent_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hold_intent"))
    private PaymentIntent intent;

    @Column(name = "amount", nullable = false)
    private long amount;

    public enum State {
        ACTIVE,
        CAPTURED,
        RELEASED,
        EXPIRED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private State state = State.ACTIVE;

    @Column(name = "expires_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime expiresAt;
}