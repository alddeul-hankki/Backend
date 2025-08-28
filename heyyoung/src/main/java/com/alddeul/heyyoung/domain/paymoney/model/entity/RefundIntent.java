package com.alddeul.heyyoung.domain.paymoney.model.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "refund_intent", uniqueConstraints = {
        @UniqueConstraint(name = "uk_refund_inst_txn_no", columnNames = "inst_txn_no")
})
public class RefundIntent extends BaseIdentityEntity {

    @Column(nullable = false, updatable = false, length = 120)
    private String email;

    @Column(nullable = false, updatable = false, length = 64)
    private String userKey;

    @Column(nullable = false, updatable = false, length = 64)
    private String accountNo;

    @Column(nullable = false, updatable = false)
    private Long amount;

    @Column(nullable = false, updatable = false, length = 100)
    private String summary;

    @Column(name = "inst_txn_no", nullable = false, updatable = false, length = 32)
    private String instTxnNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RefundStatus status;

    /**
     * 낙관적 락으로 중복 전이/중복 처리 방지
     */
    @Version
    private Long version;

    public static RefundIntent init(
            String email,
            String userKey,
            String accountNo,
            Long amount,
            String summary,
            String instTxnNo
    ) {
        validateInit(email, userKey, accountNo, amount, summary, instTxnNo);

        RefundIntent i = new RefundIntent();
        i.email = email;
        i.userKey = userKey;
        i.accountNo = accountNo;
        i.amount = amount;
        i.summary = summary;
        i.instTxnNo = instTxnNo;
        i.status = RefundStatus.INIT;
        return i;
    }

    private static void validateInit(String email, String userKey, String accountNo,
                                     Long amount, String summary, String instTxnNo) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");
        if (userKey == null || userKey.isBlank()) throw new IllegalArgumentException("userKey is required");
        if (accountNo == null || accountNo.isBlank()) throw new IllegalArgumentException("accountNo is required");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (summary == null || summary.isBlank()) throw new IllegalArgumentException("summary is required");
        if (instTxnNo == null || instTxnNo.isBlank()) throw new IllegalArgumentException("instTxnNo is required");
    }


    /**
     * 은행 계좌 입금 성공 확인: instTxnNo 일치성만 검증(초기 세팅 불변)
     */
    public void markRefundFinished(String refundTxnNo) {
        requireAnyOf(RefundStatus.PRE_DEBIT);
        if (this.instTxnNo != null && !Objects.equals(this.instTxnNo, refundTxnNo)) {
            throw new IllegalStateException("instTxnNo already set; cannot overwrite");
        }
        this.status = RefundStatus.REFUND_FINISHED;
    }

    public void markPreDebited() {
        requireStatus(RefundStatus.INIT);
        this.status = RefundStatus.PRE_DEBIT;
    }

    public void markCompensateRequired() {
        requireStatus(RefundStatus.PRE_DEBIT);
        this.status = RefundStatus.COMPENSATE_REQUIRED;
    }

    public void markCompensatedFinished() {
        requireStatus(RefundStatus.COMPENSATE_REQUIRED);
        this.status = RefundStatus.COMPENSATED_FINISHED;
    }

    private void requireStatus(RefundStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException("Invalid status: expected " + expected + " but was " + this.status);
        }
    }

    private void requireAnyOf(RefundStatus... allowed) {
        for (RefundStatus s : allowed) if (this.status == s) return;
        throw new IllegalStateException("Invalid status: " + this.status);
    }

    public enum RefundStatus {
        INIT,                   // 의도만 생성됨
        PRE_DEBIT,              // 지갑에서 선차감 (선차감 후 나중에 확정)
        REFUND_FINISHED,        // 환불 완료(정상 종료)
        COMPENSATE_REQUIRED,    // 은행 입금 실패 후, 선차감 금액 환불 필요
        COMPENSATED_FINISHED,   // 선차감 금액 환불 완료
        FAILED                  // 복원이 필요 없는 실패
    }
}
