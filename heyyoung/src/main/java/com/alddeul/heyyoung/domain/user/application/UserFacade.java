package com.alddeul.heyyoung.domain.user.application;

import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import com.alddeul.heyyoung.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserRepository userRepository;

    public String getUserKey(String email) {
        SolUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        return user.getAccessKey();
    }
}
