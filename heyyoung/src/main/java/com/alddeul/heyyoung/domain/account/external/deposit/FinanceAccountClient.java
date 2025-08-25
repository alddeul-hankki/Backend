package com.alddeul.heyyoung.domain.account.external.deposit;


import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.alddeul.heyyoung.common.api.external.utils.WebClientHelper;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FinanceAccountClient {
    private final WebClientHelper webClientHelper;

    @Value("${financial.api.base-url}")
    private String url;

    @Value("${financial.api.key}")
    private String appKey;

    @Value("${financial.api.default.account-type}")
    private String defaultAccountType;


    public FinanceApiResponse<CreateAccountResponse> createDemandDepositAccount(String userKey) {
        String apiName = "createDemandDepositAccount";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        CreateAccountRequest request = CreateAccountRequest.of(header, defaultAccountType);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/createDemandDepositAccount", request, CreateAccountResponse.class);
    }

    public FinanceApiResponse<InquireAccountListResponse> inquireDemandDepositAccountList(String userKey) {
        String apiName = "inquireDemandDepositAccountList";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        InquireAccountListRequest request = InquireAccountListRequest.of(header);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/inquireDemandDepositAccountList", request, InquireAccountListResponse.class);
    }

    public FinanceApiResponse<InquireAccountHistoryResponse> inquireTransactionHistory(String userKey, String accountNo, Long transactionNo) {
        String apiName = "inquireTransactionHistory";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        InquireAccountHistoryRequest request = InquireAccountHistoryRequest.of(header, accountNo, transactionNo);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/inquireTransactionHistory", request, InquireAccountHistoryResponse.class);
    }

    public FinanceApiResponse<WithdrawalAccountResponse> withdrawDemandDepositAccount(String userKey, String accountNo, Long transactionBalance, String transactionSummary) {
        String apiName = "withdrawDemandDepositAccount";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        WithdrawalAccountRequest request = WithdrawalAccountRequest.of(header, accountNo, transactionBalance, transactionSummary);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/updateDemandDepositAccountWithdrawal", request, WithdrawalAccountResponse.class);
    }

    public FinanceApiResponse<DepositAccountResponse> depositDemandDepositAccount(String userKey, String accountNo, Long transactionBalance, String transactionSummary) {
        String apiName = "depositDemandDepositAccount";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        DepositAccountRequest request = DepositAccountRequest.of(header, accountNo, transactionBalance, transactionSummary);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/updateDemandDepositAccountDeposit", request, DepositAccountResponse.class);
    }
}
