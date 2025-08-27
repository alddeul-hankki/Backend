package com.alddeul.heyyoung.domain.account.presentation;

import com.alddeul.heyyoung.common.api.response.ApiResponse;
import com.alddeul.heyyoung.common.api.response.ResponseCode;
import com.alddeul.heyyoung.domain.account.application.AccountAuthService;
import com.alddeul.heyyoung.domain.account.presentation.request.ConfirmAccountRequest;
import com.alddeul.heyyoung.domain.account.presentation.request.VerifyAccountRequest;
import com.alddeul.heyyoung.domain.account.presentation.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/accounts")
@RequiredArgsConstructor
public class AccountAuthController {
    private final AccountAuthService accountAuthService;

    @PostMapping("/verification")
    public ApiResponse<AccountResponse> verifyAccount(@RequestBody VerifyAccountRequest request) {
        accountAuthService.verifyAccount(request.email(),  request.accountId());
        return ApiResponse.of(ResponseCode.OK);
    }

    @PostMapping("/verification/confirm")
    public ApiResponse<Void> confirmAccount(@RequestBody ConfirmAccountRequest request) {
        accountAuthService.confirmAccount(request.email(), request.accountId(), request.code());
        return ApiResponse.of(ResponseCode.OK);
    }
}
