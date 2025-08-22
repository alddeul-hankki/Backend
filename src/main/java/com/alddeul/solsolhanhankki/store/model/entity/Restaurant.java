package com.alddeul.solsolhanhankki.store.model.entity;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "restaurant")
public class Restaurant extends BaseIdentityEntity {

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "external_store_id", length = 64) // 땡겨요 매장ID 매핑용(시뮬)
    private String externalStoreId;
}

