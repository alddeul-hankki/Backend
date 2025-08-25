package com.alddeul.solsolhanhankki.notification.domain;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fcm_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseIdentityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    @Column(nullable = false)
    private long userId;

    @Builder
    private FcmToken(String token, DeviceType deviceType, long userId) {
        this.token = token;
        this.deviceType = deviceType;
        this.userId = userId;
    }

    public static FcmToken create(String token, DeviceType deviceType, long userId) {
        return FcmToken.builder()
                .token(token)
                .deviceType(deviceType)
                .userId(userId)
                .build();
    }

    public boolean isExpired() {
        return getCreatedAt().isBefore(OffsetDateTime.now().minusDays(60));
    }

    public boolean belongsToUser(long userId) {
        return this.userId != userId;
    }
}
