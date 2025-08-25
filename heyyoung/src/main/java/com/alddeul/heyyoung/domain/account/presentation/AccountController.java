package com.alddeul.heyyoung.domain.account.presentation;

import com.alddeul.heyyoung.common.api.response.ApiResponse;
import com.alddeul.heyyoung.domain.account.application.AccountService;
import com.alddeul.heyyoung.domain.account.presentation.request.TransactionHistoryRequest;
import com.alddeul.heyyoung.domain.account.presentation.response.AccountResponse;
import com.alddeul.heyyoung.domain.account.presentation.response.AccountSummaryResponse;
import com.alddeul.heyyoung.domain.account.presentation.response.TransactionHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping()
    public ResponseEntity<List<AccountSummaryResponse>> getAccounts(@RequestParam String email) {
        var response = accountService.getAccounts(email);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping()
    public ApiResponse<AccountResponse> createAccount(@RequestParam String email) {
        var response = accountService.createAccount(email);
        return ApiResponse.created(response);
    }

    /**
     * 조회 메서드, 요청 본문 암호화하기 위해 post 메서드 이용
     */
    @PostMapping("/transactions")
    public ApiResponse<List<TransactionHistoryResponse>> getTransactionHistory(@RequestBody TransactionHistoryRequest request) {
        var response = accountService.getTransactionHistory(request.email(), request.accountId(), request.range());
        return ApiResponse.ok(response);
    }
}