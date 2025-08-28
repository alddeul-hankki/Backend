package com.alddeul.solsolhanhankki.notification.domain;

import java.time.LocalDateTime;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fcm_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseIdentityEntity {

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
        return getCreatedAt().isBefore(LocalDateTime.now().minusDays(60));
    }

    public boolean belongsToUser(long userId) {
        return this.userId != userId;
    }
}
