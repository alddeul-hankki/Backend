package com.alddeul.solsolhanhankki.order.model.entity;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseSeqEntity;
import com.alddeul.solsolhanhankki.store.model.entity.MenuItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Check;


/**
 * 그룹 주문에서 특정 유저가 담은 메뉴 1줄을 표현합니다.
 * 메뉴 1종류 = 라인 1개
 * 예) 김밥×2, 라면×1  →  라인 2개로 저장.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member", "menuItem"})
@Entity
@Table(name = "group_order_item",
        indexes = {
                @Index(name = "idx_group_order_item_member", columnList = "group_order_member_id"),
                @Index(name = "idx_group_order_item_menu", columnList = "menu_item_id")
        })
@Check(constraints = "quantity > 0 AND unit_price >= 0 AND sum_price = quantity * unit_price")
public class GroupOrderItem extends BaseSeqEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_order_member_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_order_item_member"))
    private GroupOrderMember member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_order_item_menu_item"))
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity; // 주문 수량

    @Column(name = "unit_price", nullable = false)
    private long unitPrice; // 단가

    @Column(name = "sum_price", nullable = false)
    private long sumPrice; // 합계 금액
}
