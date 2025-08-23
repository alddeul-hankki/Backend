package com.alddeul.solsolhanhankki.order.model.entity;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.solsolhanhankki.user.model.entity.SolUser;
import com.alddeul.solsolhanhankki.wallet.model.entity.WalletHold;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 그룹 주문에 들어간 유저의 엔티티 입니다
 * 그룹 주문에 들어갔으면 돈이 hold되고
 * heldAmount는 그 hold된 값의 합입니다.
 * WalletHold.amount랑 같은데 조회 편의를 위해 넣었습니다.
 */
@Entity
@Table(name = "group_order_member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_per_user", columnNames = {"group_order_id", "user_id"}),
                @UniqueConstraint(name = "uk_member_hold", columnNames = {"hold_id"})
        },
        indexes = {
                @Index(name = "idx_group_order_member_group", columnList = "group_order_id"),
                @Index(name = "idx_group_order_member_user", columnList = "user_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"groupOrder", "user", "hold", "items"})
public class GroupOrderMember extends BaseIdentityEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_order_member_group"))
    private GroupOrder groupOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_order_member_user"))
    private SolUser user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hold_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_order_member_hold"))
    private WalletHold hold;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupOrderItem> items = new ArrayList<>();

    @Column(name = "held_amount", nullable = false)
    private long heldAmount;

    public enum Status {
        JOINED,
        PROCESSING,
        CANCELED,
        CAPTURED,
        REFUNDED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private Status status = Status.JOINED;
}
