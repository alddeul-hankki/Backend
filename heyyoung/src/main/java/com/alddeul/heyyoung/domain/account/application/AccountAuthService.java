package com.alddeul.heyyoung.domain.account.application;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.domain.account.external.auth.FinanceAccountAuthClient;
import com.alddeul.heyyoung.domain.account.external.auth.dto.CheckAuthCodeResponse;
import com.alddeul.heyyoung.domain.account.external.auth.dto.OneCentTransferResponse;
import com.alddeul.heyyoung.domain.account.model.entity.Account;
import com.alddeul.heyyoung.domain.account.model.repository.AccountRepository;
import com.alddeul.heyyoung.domain.user.application.UserFacade;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountAuthService {
    private final FinanceAccountAuthClient financeAccountAuthClient;
    private final AccountRepository accountRepository;
    private final UserFacade userFacade;

    public void verifyAccount(String email, Long accountId) {
        SolUser user = userFacade.getUser(email);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 계좌입니다."));

        if (!user.equals(account.getSolUser())) {
            throw new RuntimeException("본인의 계좌에만 접근할 수 있습니다.");
        }

        if (account.getVerifyStatus() == Account.VerifyStatus.VERIFIED) {
            throw new RuntimeException("이미 인증된 계좌입니다.");
        }

        FinanceApiResponse<OneCentTransferResponse> response =
                financeAccountAuthClient.openAccountAuth(user.getAccessKey(), account.getAccountNumber());

        if (!response.success()) {
            log.error("[{}]: {}", response.error().responseCode(), response.error().responseMessage());
            throw new RuntimeException(response.error().responseMessage());
        }
    }


    public void confirmAccount(String email, Long accountId, String code) {
        SolUser user = userFacade.getUser(email);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 계좌입니다."));

        if (!user.equals(account.getSolUser())) {
            throw new RuntimeException("본인의 계좌에만 접근할 수 있습니다.");
        }

        if (account.getVerifyStatus() == Account.VerifyStatus.VERIFIED) {
            throw new RuntimeException("이미 인증된 계좌입니다.");
        }

        FinanceApiResponse<CheckAuthCodeResponse> response =
                financeAccountAuthClient.checkAuthCode(user.getAccessKey(), account.getAccountNumber(), code);

        if (!response.success()) {
            log.error("[{}]: {}", response.error().responseCode(), response.error().responseMessage());
            throw new RuntimeException(response.error().responseMessage());
        }

        account.confirm();
    }
}
