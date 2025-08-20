package com.alddeul.solsolhanhankki.store.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "restaurant")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "external_store_id", length = 64) // 땡겨요 매장ID 매핑용(시뮬)
    private String externalStoreId;
}

