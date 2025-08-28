package com.alddeul.heyyoung.domain.paymoney.model.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "topup_intent", uniqueConstraints = {
        @UniqueConstraint(name = "uk_topup_inst_txn_no", columnNames = "inst_txn_no")
})
public class TopUpIntent extends BaseIdentityEntity {

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
    private TopUpStatus status;

    /**
     * 낙관적 락으로 중복 전이/중복 크레딧 방지
     */
    @Version
    private Long version;

    public static TopUpIntent init(
            String email,
            String userKey,
            String accountNo,
            Long amount,
            String summary,
            String instTxnNo
    ) {
        validateInit(email, userKey, accountNo, amount, summary, instTxnNo);

        TopUpIntent i = new TopUpIntent();
        i.email = email;
        i.userKey = userKey;
        i.accountNo = accountNo;
        i.amount = amount;
        i.summary = summary;
        i.instTxnNo = instTxnNo;
        i.status = TopUpStatus.INIT;
        return i;
    }

    private static void validateInit(String email, String userKey, String accountNo, Long amount, String summary, String instTxnNo) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");
        if (userKey == null || userKey.isBlank()) throw new IllegalArgumentException("userKey is required");
        if (accountNo == null || accountNo.isBlank()) throw new IllegalArgumentException("accountNo is required");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (summary == null || summary.isBlank()) throw new IllegalArgumentException("summary is required");
        if (instTxnNo == null || instTxnNo.isBlank()) throw new IllegalArgumentException("instTxnNo is required");
    }

    /**
     * 은행 출금 성공 시 호출: instTxnNo 최초 1회만 세팅 허용
     */
    public void markDebitConfirmed(String instTxnNo) {
        requireStatus(TopUpStatus.INIT);
        if (this.instTxnNo != null && !Objects.equals(this.instTxnNo, instTxnNo)) {
            throw new IllegalStateException("instTxnNo already set; cannot overwrite");
        }
        this.status = TopUpStatus.DEBIT_CONFIRMED;
    }

    /**
     * 명시적 실패
     */
    public void markFailed() {
        requireStatus(TopUpStatus.INIT);
        this.status = TopUpStatus.FAILED;
    }

    /**
     * 페이머니 충전 완료 (은행 확정 이후에만 허용)
     */
    public void markCredited() {
        requireStatus(TopUpStatus.DEBIT_CONFIRMED);
        this.status = TopUpStatus.CREDIT_CONFIRMED;
    }

    public void markCompensateRequired() {
        this.status = TopUpStatus.COMPENSATE_REQUIRED;
    }

    public void markCompensated(String instTxnNo) {
        requireStatus(TopUpStatus.COMPENSATE_REQUIRED);
        if (this.instTxnNo != null && !Objects.equals(this.instTxnNo, instTxnNo)) {
            throw new IllegalStateException("instTxnNo already set; cannot overwrite");
        }
        this.status = TopUpStatus.COMPENSATED;
    }

    private void requireStatus(TopUpStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException("Invalid status: expected " + expected + " but was " + this.status);
        }
    }

    public enum TopUpStatus {
        INIT,                 // 의도 생성
        DEBIT_CONFIRMED,      // 은행 출금 성공 (지갑 충전 대기)
        CREDIT_CONFIRMED,     // 지갑 충전 성공 (정상 종료)
        COMPENSATE_REQUIRED,  // 지갑 충전 실패 → 은행 출금 보상 필요(재입금)
        COMPENSATED,          // 보상 완료
        FAILED                // 명시적 실패(보상 불필요)
    }
}
