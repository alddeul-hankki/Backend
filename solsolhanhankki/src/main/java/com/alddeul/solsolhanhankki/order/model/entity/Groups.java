package com.alddeul.solsolhanhankki.order.model.entity;

import com.alddeul.solsolhanhankki.campus.model.entity.PickupZone;
import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.solsolhanhankki.order.model.enums.GroupStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "groups", indexes = {
        @Index(name = "idx_status_triggered_at", columnList = "status, triggeredAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Groups extends BaseIdentityEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_zone_id", nullable = false)
    private PickupZone pickupZone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status = GroupStatus.RECRUITING;

    @Column(nullable = false)
    private String storeId;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private Long minOrderPrice;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "group_delivery_fee_policies", joinColumns = @JoinColumn(name = "group_id"))
    private List<DeliveryFeePolicy> deliveryFeePolicies;

    @Column(nullable = false)
    private Long triggerPrice;

    @Column(nullable = false)
    private Long currentTotalPrice = 0L;

    @Column(nullable = false)
    private Integer participantCount = 0;

    @Column(nullable = false)
    private OffsetDateTime deadlineAt;

    private OffsetDateTime triggeredAt;

    @Column(nullable = false)
    private OffsetDateTime pickupAt;

    @Version
    private Long version;

    @Builder
    public Groups(PickupZone pickupZone, String storeId, String storeName, Long minOrderPrice, List<DeliveryFeePolicy> deliveryFeePolicies, Long triggerPrice, OffsetDateTime deadlineAt, OffsetDateTime pickupAt) {
        this.pickupZone = pickupZone;
        this.storeId = storeId;
        this.storeName = storeName;
        this.minOrderPrice = minOrderPrice;
        this.deliveryFeePolicies = deliveryFeePolicies;
        this.triggerPrice = triggerPrice;
        this.deadlineAt = deadlineAt;
        this.pickupAt = pickupAt;
    }

    /**
     * 현재 주문 총액을 기준으로 적용될 '그룹 전체' 배달비를 계산합니다.
     */
    public Long getCurrentDeliveryFee() {
        if (this.deliveryFeePolicies == null || this.deliveryFeePolicies.isEmpty()) {
            return 0L;
        }

        return deliveryFeePolicies.stream()
                .sorted(Comparator.comparing(DeliveryFeePolicy::getOrderAmountThreshold).reversed())
                .filter(policy -> this.currentTotalPrice >= policy.getOrderAmountThreshold())
                .findFirst()
                .map(DeliveryFeePolicy::getFee)
                .orElseGet(() ->
                        deliveryFeePolicies.stream()
                                .min(Comparator.comparing(DeliveryFeePolicy::getOrderAmountThreshold))
                                .map(DeliveryFeePolicy::getFee)
                                .orElse(0L) // 정책은 있지만 비어있는 극단적인 경우 0을 반환
                );
    }

    /**
     * 새로운 주문이 들어왔을 때, 예상되는 '1인당' 배달비를 계산합니다.
     */
    public Long calculateExpectedFee(Long newMenuPrice) {
        if (this.deliveryFeePolicies == null || this.deliveryFeePolicies.isEmpty()) return 0L;

        // 내가 참여했을 경우의 총 금액
        long totalPriceAfterJoin = this.currentTotalPrice + newMenuPrice;

        // 예상되는 그룹 전체 배달비 계산
        long totalFeeAfterJoin = deliveryFeePolicies.stream()
                .sorted(Comparator.comparing(DeliveryFeePolicy::getOrderAmountThreshold).reversed())
                .filter(policy -> totalPriceAfterJoin >= policy.getOrderAmountThreshold())
                .findFirst()
                .map(DeliveryFeePolicy::getFee)
                .orElseGet(() ->
                        deliveryFeePolicies.stream()
                                .min(Comparator.comparing(DeliveryFeePolicy::getOrderAmountThreshold))
                                .map(DeliveryFeePolicy::getFee)
                                .orElse(0L)
                );

        // 예상 1/N 배달비 반환
        return totalFeeAfterJoin / (this.participantCount + 1);
    }

    public void addParticipant(Long menuTotalPrice) {
        this.currentTotalPrice += menuTotalPrice;
        this.participantCount++;
    }

    public void removeParticipant(Long menuTotalPrice) {
        this.currentTotalPrice -= menuTotalPrice;
        this.participantCount--;
    }

    public void completeOrder() {
        this.status = GroupStatus.ORDERED;
    }

    public Long getPerPersonDeliveryFee() {
        if (this.participantCount == null || this.participantCount == 0) {
            return getCurrentDeliveryFee(); // 참여자가 없으면 그룹 전체 배달비를 반환 (1인분)
        }
        return getCurrentDeliveryFee() / this.participantCount;
    }

    public boolean isJoinable() {
        return this.status == GroupStatus.RECRUITING || this.status == GroupStatus.TRIGGERED;
    }

    public void cancel() {
        this.status = GroupStatus.CANCELED;
    }

    public boolean isTriggered() {
        return this.currentTotalPrice >= this.triggerPrice;
    }

    public void trigger() {
        this.status = GroupStatus.TRIGGERED;
        this.triggeredAt = OffsetDateTime.now(); // 트리거된 시간 기록
        this.deadlineAt = OffsetDateTime.now().plusMinutes(5); // 마감 시간을 '지금으로부터 5분 뒤'로 변경
    }
}