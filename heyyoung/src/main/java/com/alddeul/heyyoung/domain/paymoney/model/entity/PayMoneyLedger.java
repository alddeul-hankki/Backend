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
@Table(
        name = "paymoney_ledger",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ledger_paymoney_idem",
                columnNames = {"paymoney_id","idempotency_key"}
        )
)
@Immutable
@Check(constraints = "amount > 0 AND balance_after >= 0")
public class PayMoneyLedger extends BaseSeqEntity {

    // 어떤 지갑의 거래인지
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paymoney_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ledger_paymoney"))
    private PayMoney paymoney;

    public enum PayMoneyLedgerType {
        DEPOSIT_CREDIT,   // 입금/충전
        WITHDRAW_DEBIT,   // 출금/환불

        PURCHASE_DEBIT,   // 결제 (지갑 차감)
        REFUND_CREDIT,    // 환불 (지갑 환급)

        ADJUSTMENT_DEBIT,  // 운영자 수동 차감
        ADJUSTMENT_CREDIT  // 운영자 수동 가산
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PayMoneyLedgerType type;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "amount", nullable = false)
    private long amount;

    // 거래 후 지갑 잔액
    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    @Column(name = "idempotency_key", nullable = false, length = 128, updatable = false)
    private String idempotencyKey;

    // SSAFY API -> paymoney
    public static PayMoneyLedger deposit(PayMoney w, long amt, String idem, String description) {
        return create(w, PayMoneyLedgerType.DEPOSIT_CREDIT, amt, w.getAmount(), idem, description);
    }
    // paymoney -> SSAFY API
    public static PayMoneyLedger withdraw(PayMoney w, long amt, String idem, String description) {
        return create(w, PayMoneyLedgerType.WITHDRAW_DEBIT, amt, w.getAmount(), idem, description);
    }
    public static PayMoneyLedger purchase(PayMoney w, long amt, String idem, String description) {
        return create(w, PayMoneyLedgerType.PURCHASE_DEBIT, amt, w.getAmount(), idem, description);
    }
    public static PayMoneyLedger refund(PayMoney w, long amt, String idem, String description) {
        return create(w, PayMoneyLedgerType.REFUND_CREDIT, amt, w.getAmount(), idem, description);
    }

    private static PayMoneyLedger create(PayMoney w, PayMoneyLedgerType t, long amt, long balAfter, String idem, String description) {
        if (w == null) throw new IllegalArgumentException("wallet null");
        if (amt <= 0) throw new IllegalArgumentException("amount must be positive");
        if (balAfter < 0) throw new IllegalArgumentException("balance_after >= 0");
        PayMoneyLedger l = new PayMoneyLedger();
        l.paymoney = w;
        l.type = t;
        l.description = description;
        l.amount = amt;
        l.balanceAfter = balAfter;
        l.idempotencyKey = idem;
        return l;
    }
}