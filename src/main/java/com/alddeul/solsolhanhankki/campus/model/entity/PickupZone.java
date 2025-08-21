package com.alddeul.solsolhanhankki.campus.model.entity;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pickup_zone",
        uniqueConstraints = @UniqueConstraint(name = "uk_pickup_zone_name", columnNames = {"campus_code", "label"}),
        indexes = {
                @Index(name = "idx_pickup_campus", columnList = "campus_code")
        })
public class PickupZone extends BaseIdentityEntity {

    // 캠퍼스 식별 코드
    @Column(name = "campus_code", nullable = false, length = 32)
    private String campusCode;

    // 픽업존 이름
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 지도 마커 표시용 좌표
    @Column(name = "lat", nullable = false)
    private double lat;

    @Column(name = "lng", nullable = false)
    private double lng;
}
