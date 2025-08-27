package com.alddeul.solsolhanhankki.notification.repository;

import com.alddeul.solsolhanhankki.notification.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByToken(String token);
    List<FcmToken> findByUserId(long userId);
    void deleteByToken(String token);
}