package com.alddeul.heyyoung.domain.paymoney.model.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseSeqEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Immutable;


/**
 * 지갑의 모든 입출급 내역을 기록하는 엔티티 입니다.
 * 불변 객체입니다.
 * PayMoneyLedgerType으로 가산 / 차감을 구분하기 때문에 amount > 0 입니다.
 * 가산 타입의 amount면 +, 차감 타입의 amount면 - 되는 형식입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "paymoney_ledger",
        indexes = {
                @Index(name = "idx_ledger_paymoney_created", columnList = "paymoney_id, created_at"),
                @Index(name = "idx_ledger_intent", columnList = "intent_id"),
                @Index(name = "idx_ledger_hold", columnList = "hold_id")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_ledger_idempotency", columnNames = "idempotency_key")
)
@Immutable
@Check(constraints = "amount > 0 AND balance_after >= 0")
public class PayMoneyLedger extends BaseSeqEntity {

    // 어떤 지갑의 거래인지
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paymoney_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ledger_paymoney"))
    private PayMoney paymoney;

    // 어떤 인텐트/홀드와 연계된 거래인지 (관리자 수정 등으로 없을 수도 있습니다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intent_id",
            foreignKey = @ForeignKey(name = "fk_ledger_intent"))
    private PaymentIntent intent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hold_id",
            foreignKey = @ForeignKey(name = "fk_ledger_hold"))
    private PayMoneyHold hold;

    public enum PayMoneyLedgerType {
        HOLD_DEBIT,            // 예약 홀드로 지갑에서 차감
        HOLD_RELEASE_CREDIT,   // 홀드 해제(만료/실패)로 지갑 환원
        CAPTURE_DEBIT,         // 공동주문 확정 시 최종 차감
        REFUND_CREDIT,         // 점주 취소 등 환불로 지갑 환원
        ADJUSTMENT_DEBIT,      // 운영/정산 조정(차감)
        ADJUSTMENT_CREDIT      // 운영/정산 조정(가산)
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PayMoneyLedgerType type;

    @Column(name = "amount", nullable = false)
    private long amount;

    // 거래 후 지갑 잔액
    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    @Column(name = "idempotency_key", nullable = false, length = 128, updatable = false)
    private String idempotencyKey;
}