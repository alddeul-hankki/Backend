package com.alddeul.solsolhanhankki.wallet.model.entity;

import com.alddeul.solsolhanhankki.user.model.entity.SolUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

// 지갑 엔티티 (신한Pay머니)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pay_money_wallet",
        uniqueConstraints = @UniqueConstraint(name = "uk_wallet_user", columnNames = "user_id"))
@Check(constraints = "amount >= 0")
public class PayMoneyWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_wallet_user"))
    private SolUser user;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Version
    @Column(nullable = false)
    private long version;

    @UpdateTimestamp
    @Column(name = "updated_at",
            columnDefinition = "timestamptz NOT NULL DEFAULT now()",
            insertable = false)
    private OffsetDateTime updatedAt;
}
