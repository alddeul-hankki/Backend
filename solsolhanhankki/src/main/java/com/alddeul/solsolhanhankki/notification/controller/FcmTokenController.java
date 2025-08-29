package com.alddeul.solsolhanhankki.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alddeul.solsolhanhankki.notification.dto.FcmTokenDto;
import com.alddeul.solsolhanhankki.notification.service.FcmTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/sol/api/fcm")
@CrossOrigin(origins = {"http://localhost:4173"})
public class FcmTokenController {
	private final FcmTokenService fcmTokenService;
	
	@PostMapping("/token")
	public ResponseEntity<FcmTokenDto.Response> saveFcmToken(@RequestBody @Valid FcmTokenDto.SaveRequest request) {
        log.info("FCM 토큰 저장 요청: deviceType={}", request.deviceType());
        
        FcmTokenDto.Response response = fcmTokenService.saveFcmToken(request);
        
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
	
	@DeleteMapping("/token")
	public ResponseEntity<FcmTokenDto.Response> deleteFcmToken(@RequestBody FcmTokenDto.DeleteRequest request) {
        log.info("FCM 토큰 삭제 요청: token={}", request.fcmToken());
        
        FcmTokenDto.Response response = fcmTokenService.deleteFcmToken(request.fcmToken());
        
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
