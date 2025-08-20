package com.alddeul.solsolhanhankki.store.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "menu_item",
        indexes = @Index(name = "idx_menu_restaurant", columnList = "restaurant_id"))
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_menu_restaurant"))
    private Restaurant restaurant;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private long price;
}
