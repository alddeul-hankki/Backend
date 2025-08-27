package com.alddeul.solsolhanhankki.notification.dto;

import java.time.OffsetDateTime;

import com.alddeul.solsolhanhankki.notification.domain.DeviceType;
import com.alddeul.solsolhanhankki.notification.domain.FcmToken;

public class FcmTokenDto {

    public static record SaveRequest(
            String fcmToken,
            String deviceType,
            OffsetDateTime timestamp,
            long userId) {

        public DeviceType getDeviceTypeEnum() {
            return DeviceType.valueOf(deviceType.toUpperCase());
        }
    }

    public static record DeleteRequest(
            String fcmToken
            ) {}

    public static record Response(
            boolean success,
            String message,
            String fcmToken
            ) {

        public static Response success(FcmToken fcmToken) {
            return new Response(
                    true,
                    "FCM 토큰이 성공적으로 저장되었습니다.",
                    fcmToken.getToken()
                    );
        }

        public static Response fail(String message) {
            return new Response(
                    false,
                    message,
                    null
                    );
        }

        public static Response deleteSuccess() {
            return new Response(
                    true,
                    "FCM 토큰이 성공적으로 삭제되었습니다.",
                    null
            		);
        }
    }
}
