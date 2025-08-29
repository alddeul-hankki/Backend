package com.alddeul.solsolhanhankki.order.model.entity;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.solsolhanhankki.order.model.enums.OrderStatus;
import com.alddeul.solsolhanhankki.order.model.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends BaseIdentityEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Groups group;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItems> orderItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Long menuTotalPrice;

    @Column(nullable = false)
    private Long initialDeliveryFee;

    private Long finalDeliveryFee;
    private Long refundAmount;

    @Column(nullable = false, unique = true)
    private String paymentToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus = RefundStatus.NONE;

    @Builder
    public Orders(Groups group, Long userId, Long menuTotalPrice, Long initialDeliveryFee) {
        this.group = group;
        this.userId = userId;
        this.menuTotalPrice = menuTotalPrice;
        this.initialDeliveryFee = initialDeliveryFee;
    }

    public void finalizeFee(long perPersonFee) {
        this.finalDeliveryFee = perPersonFee;
        this.refundAmount = this.initialDeliveryFee - this.finalDeliveryFee;
        if (this.refundAmount < 0) this.refundAmount = 0L;
        this.refundStatus = (this.refundAmount > 0) ? RefundStatus.PENDING : RefundStatus.NONE;
    }

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    // 임시 랜덤토큰 발급
    @PrePersist
    public void createPaymentToken() {
        this.paymentToken = UUID.randomUUID().toString();
    }
    
}