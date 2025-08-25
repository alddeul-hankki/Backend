package com.alddeul.solsolhanhankki.user.application;

import com.alddeul.solsolhanhankki.user.model.entity.SolUser;
import com.alddeul.solsolhanhankki.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void registerUserAtBank(String email, String userKey) {
        SolUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.registerUserAtBank(userKey);
    }
}
