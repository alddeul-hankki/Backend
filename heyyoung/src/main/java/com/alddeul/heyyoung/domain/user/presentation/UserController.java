package com.alddeul.heyyoung.domain.user.presentation;

import com.alddeul.heyyoung.common.api.response.ApiResponse;
import com.alddeul.heyyoung.domain.user.application.UserAccountService;
import com.alddeul.heyyoung.domain.user.presentation.request.UserRequest;
import com.alddeul.heyyoung.domain.user.presentation.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserAccountService userAccountService;

    @PostMapping()
    public ApiResponse<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        var response = userAccountService.createUser(userRequest);
        return ApiResponse.ok(response);
    }
}
