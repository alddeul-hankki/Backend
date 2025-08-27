package com.alddeul.solsolhanhankki.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alddeul.solsolhanhankki.notification.domain.FcmToken;
import com.alddeul.solsolhanhankki.notification.dto.FcmTokenDto;
import com.alddeul.solsolhanhankki.notification.repository.FcmTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    
    @Transactional
    public FcmTokenDto.Response saveFcmToken(FcmTokenDto.SaveRequest request) {
    	try {
            fcmTokenRepository.findByToken(request.fcmToken())
                    .ifPresent(existingToken ->fcmTokenRepository.deleteByToken(request.fcmToken()));
            fcmTokenRepository.flush();
            
            FcmToken fcmToken = FcmToken.create(
                    request.fcmToken(),
                    request.getDeviceTypeEnum(),
                    request.userId()
            );

            FcmToken savedToken = fcmTokenRepository.save(fcmToken);

            log.info("FCM 토큰 저장 성공: userId={}, deviceType={}", 
                    request.userId(), request.getDeviceTypeEnum());

            return FcmTokenDto.Response.success(savedToken);
            
        } catch (Exception e) {
            log.error("FCM 토큰 저장 실패: {}", e.getMessage(), e);
            return FcmTokenDto.Response.fail("FCM 토큰 저장에 실패했습니다: " + e.getMessage());
        }
    }
    
    @Transactional
    public FcmTokenDto.Response deleteFcmToken(String fcmToken) {
        try {
            fcmTokenRepository.deleteByToken(fcmToken);

            log.info("FCM 토큰 삭제 성공: token={}", fcmToken);

            return FcmTokenDto.Response.deleteSuccess();

        } catch (Exception e) {
            log.error("FCM 토큰 삭제 실패: {}", e.getMessage(), e);
            return FcmTokenDto.Response.fail("FCM 토큰 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getFcmTokensByUserId(long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }
}