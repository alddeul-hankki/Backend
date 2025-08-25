package com.alddeul.solsolhanhankki.user.application;

import com.alddeul.solsolhanhankki.global.client.dto.ApiResponse;
import com.alddeul.solsolhanhankki.user.client.UserClient;
import com.alddeul.solsolhanhankki.user.client.dto.UserApiResponse;
import com.alddeul.solsolhanhankki.user.presentation.request.UserRequest;
import com.alddeul.solsolhanhankki.user.presentation.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserService userService;
    private final UserClient userClient;

    public UserResponse createUser(UserRequest userRequest) {
        String email = userRequest.email();

        // 1. 사용자 검색
        ApiResponse<UserApiResponse> searchResponse = userClient.searchUser(email);
        if (searchResponse.success()) {
            return UserResponse.of(UserResponse.Status.ALREADY_REGISTERED);
        }

        // 2. 앱 등록하지 않은 사용자 → 등록 시도
        if ("E4003".equals(searchResponse.error().responseCode())) {
            ApiResponse<UserApiResponse> loginResponse = userClient.loginUser(email);
            if (loginResponse.success()) {
                userService.registerUserAtBank(email, loginResponse.data().userKey());
                return UserResponse.of(UserResponse.Status.REGISTERED);
            }
            return UserResponse.of(UserResponse.Status.REGISTRATION_FAILED, loginResponse.error().responseMessage());
        }

        // 3. 기타 실패
        return UserResponse.of(UserResponse.Status.REGISTRATION_FAILED, searchResponse.error().responseMessage());
    }
}
