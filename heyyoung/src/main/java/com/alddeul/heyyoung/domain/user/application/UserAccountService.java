package com.alddeul.heyyoung.domain.user.application;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.common.api.external.dto.ErrorCode;
import com.alddeul.heyyoung.domain.user.external.FinanceUserApiClient;
import com.alddeul.heyyoung.domain.user.external.dto.FinanceUserResponse;
import com.alddeul.heyyoung.domain.user.presentation.request.UserRequest;
import com.alddeul.heyyoung.domain.user.presentation.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserService userService;
    private final FinanceUserApiClient financeUserClient;

    public UserResponse createUser(UserRequest userRequest) {
        String email = userRequest.email();

        // 1. 사용자 검색
        FinanceApiResponse<FinanceUserResponse> searchResponse = financeUserClient.searchUser(email);
        if (searchResponse.success()) {
            return UserResponse.of(UserResponse.Status.ALREADY_REGISTERED);
        }

        // 2. 앱 등록하지 않은 사용자 → 등록 시도
        if (ErrorCode.E4003.equals(searchResponse.error().responseCode())) {
            FinanceApiResponse<FinanceUserResponse> loginResponse = financeUserClient.loginUser(email);
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
