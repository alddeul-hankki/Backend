package com.alddeul.heyyoung.domain.paymoney.model.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

// 지갑 엔티티 (신한Pay머니)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pay_money_paymoney",
        uniqueConstraints = @UniqueConstraint(name = "uk_paymoney_user", columnNames = "user_id"))
@Check(constraints = "amount >= 0")
public class PayMoney extends BaseIdentityEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_paymoney_user"))
    private SolUser user;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Version
    @Column(nullable = false)
    private long version;
}