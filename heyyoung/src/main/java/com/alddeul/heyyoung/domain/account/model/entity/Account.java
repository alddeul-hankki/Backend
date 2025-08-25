package com.alddeul.heyyoung.domain.account.model.entity;


import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account",
        indexes = {@Index(name = "idx_account_number", columnList = "account_number")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseIdentityEntity {
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_type_name")
    private String accountTypeName;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "is_primary")
    private Boolean primaryAccount;

    @Column(name = "verify_status")
    private VerifyStatus verifyStatus;

    public enum VerifyStatus {
        PENDING,
        VERIFIED,
        FAILED
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sol_user_id", nullable = false)
    private SolUser solUser;

    @Builder
    private Account(String accountNumber, String accountTypeName, String bankCode, String bankName, Boolean primaryAccount, SolUser user) {
        this.accountNumber = accountNumber;
        this.accountTypeName = accountTypeName;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.primaryAccount = primaryAccount;
        this.solUser = user;
        this.verifyStatus = VerifyStatus.PENDING;
    }

}