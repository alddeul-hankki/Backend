package com.alddeul.solsolhanhankki.order.model.entity;

import com.alddeul.solsolhanhankki.campus.model.entity.PickupZone;
import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.solsolhanhankki.store.model.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

// 그룹 주문 엔티티 입니다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "group_order",
        indexes = {
                @Index(name = "idx_group_order_status_cutoff", columnList = "status, cutoff_at"),
                @Index(name = "idx_group_order_restaurant", columnList = "restaurant_id"),
                @Index(name = "idx_group_order_pickup", columnList = "pickup_zone_id")
        })
@Check(constraints = "min_amount >= 0")
public class GroupOrder extends BaseIdentityEntity {

    // 마감 시각(이 시각까지 최소금액 못 채우면 실패)
    @Column(name = "cutoff_at", nullable = false)
    private OffsetDateTime cutoffAt;

    // 최소 결제 합계 – 달성 시 결제
    @Column(name = "min_amount", nullable = false)
    private long minAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_order_restaurant"))
    private Restaurant restaurant;

    // 픽업존
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pickup_zone_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_order_pickup"))
    private PickupZone pickupZone;

    @OneToMany(mappedBy = "groupOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupOrderMember> members = new ArrayList<>();

    public enum Status {
        FORMING,        // 모집중
        CONFIRMED,      // 최소금액 달성 → 결제 진행
        FAILED,         // 미달성/점주 거절 등
        CANCELED,       // 개설자 취소
        SUBMITTED       // 외부주문 전송 완료
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.FORMING;

    @Version
    @Column(nullable = false)
    private long version;
}
