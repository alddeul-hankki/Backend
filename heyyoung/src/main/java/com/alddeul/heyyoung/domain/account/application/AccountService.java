package com.alddeul.heyyoung.domain.account.application;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.FinanceAccountClient;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.CreateAccountResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.InquireAccountListResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.InquireAccountResponse;
import com.alddeul.heyyoung.domain.account.model.entity.Account;
import com.alddeul.heyyoung.domain.account.model.repository.AccountRepository;
import com.alddeul.heyyoung.domain.account.presentation.response.AccountResponse;
import com.alddeul.heyyoung.domain.account.presentation.response.AccountSummaryResponse;
import com.alddeul.heyyoung.domain.user.application.UserFacade;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final FinanceAccountClient accountClient;
    private final UserFacade userFacade;
    private final AccountRepository accountRepository;

    /**
     * 계좌 목록을 조회한다.
     */
    public List<AccountSummaryResponse> getAccounts(String email) {
        // 1. 사용자 조회
        String userKey = userFacade.getUserKey(email);

        // 2. 외부 API 호출
        FinanceApiResponse<InquireAccountListResponse> apiResponse = accountClient.inquireDemandDepositAccountList(userKey);
        if (!apiResponse.success()) {
            throw new RuntimeException("계좌를 조회하는데 실패했습니다.");
        }

        List<InquireAccountListResponse.Rec> recs = apiResponse.data().getRec();
        if (recs.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. DB에서 계좌 조회 후 Map 생성
        Map<String, Account> accountMap = accountRepository.findByAccountNumberIn(
                recs.stream().map(InquireAccountListResponse.Rec::getAccountNo).toList()
        ).stream().collect(Collectors.toMap(Account::getAccountNumber, Function.identity()));

        // 4. DTO 변환
        return recs.stream()
                .map(rec -> AccountSummaryResponse.from(accountMap.get(rec.getAccountNo())))
                .toList();
    }


    /**
     * 계좌를 생성한다.
     */
    public AccountResponse createAccount(String email) {
        SolUser user = userFacade.getUser(email);

        // 1. 계좌 생성 API 호출
        FinanceApiResponse<CreateAccountResponse> createResponse = accountClient.createDemandDepositAccount(user.getAccessKey());

        if (!createResponse.success()) {
            throw new RuntimeException("계좌를 생성하는데 실패했습니다.");
        }

        CreateAccountResponse.Rec rec = createResponse.data().getRec();

        // 2. 계좌 상세 조회 API 호출 (bankName, accountTypeName 등 가져오기)
        FinanceApiResponse<InquireAccountResponse> detailResponse = accountClient.inquireDemandDepositAccount(user.getAccessKey(), rec.getAccountNo());

        if (!detailResponse.success()) {
            throw new RuntimeException("계좌 상세 조회 실패");
        }

        InquireAccountResponse detail = detailResponse.data();

        // 3. 엔티티 생성
        Account account = Account.builder()
                .accountNumber(detail.getRec().getAccountNo())
                .bankCode(detail.getRec().getBankCode())
                .bankName(detail.getRec().getBankName())
                .accountTypeName(detail.getRec().getAccountTypeName())
                .primaryAccount(true)
                .user(user)
                .build();

        // 4. DB 저장
        accountRepository.save(account);
        return AccountResponse.from(account);
    }
}
