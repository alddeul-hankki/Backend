package com.alddeul.solsolhanhankki.wallet.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.OffsetDateTime;

// 사용자가 주문 예약 시 hold 정보 엔티티
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "wallet_hold",
        indexes = {
                @Index(name = "idx_hold_wallet_state", columnList = "wallet_id, state"),
                @Index(name = "idx_hold_expires", columnList = "expires_at")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_hold_intent", columnNames = "intent_id"))
@Check(constraints = "amount >= 0")
public class WalletHold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hold_wallet"))
    private PayMoneyWallet wallet;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intent_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hold_intent"))
    private PaymentIntent intent;

    @Column(name = "amount", nullable = false)
    private long amount;

    public enum State {ACTIVE, CAPTURED, RELEASED, EXPIRED}

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private State state = State.ACTIVE;

    @Column(name = "expires_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime expiresAt;

    @Column(name = "created_at",
            columnDefinition = "timestamptz NOT NULL DEFAULT now()",
            insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
