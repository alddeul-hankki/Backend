package com.alddeul.solsolhanhankki.order.model.entity;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.solsolhanhankki.order.presentation.request.OrderItemRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItems extends BaseIdentityEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Column(nullable = false)
    private String menuId;

    @Column(nullable = false)
    private String menuName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String options;

    @Column(nullable = false)
    private Long pricePerItem;

    @Column(nullable = false)
    private Integer quantity = 1;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "order_item_categories", joinColumns = @JoinColumn(name = "order_item_id"))
    @Column(name = "category")
    private List<String> category;

    @Builder
    public OrderItems(Orders order, String menuId, String menuName, String options, Long pricePerItem, Integer quantity) {
        this.order = order;
        this.menuId = menuId;
        this.menuName = menuName;
        this.options = options;
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
    }

    public static OrderItems from(Orders order, OrderItemRequest request) {
        return OrderItems.builder()
                .order(order)
                .menuId(request.menuId())
                .menuName(request.menuName())
                .options(request.options())
                .pricePerItem(request.pricePerItem())
                .quantity(request.quantity())
                .build();
    }

    public static List<OrderItems> from(Orders order, List<OrderItemRequest> requests) {
        return requests.stream()
                .map(request -> from(order, request))
                .collect(Collectors.toList());
    }
}