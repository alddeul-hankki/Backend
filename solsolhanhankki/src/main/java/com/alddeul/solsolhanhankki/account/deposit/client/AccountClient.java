package com.alddeul.solsolhanhankki.account.deposit.client;

import com.alddeul.solsolhanhankki.account.deposit.client.dto.*;
import com.alddeul.solsolhanhankki.global.client.dto.RequestHeader;
import com.alddeul.solsolhanhankki.global.client.util.WebClientHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountClient {
    private final WebClientHelper webClientHelper;

    @Value("${financial.api.base-url}")
    private String url;

    @Value("${financial.api.key}")
    private String appKey;

    @Value("${financial.api.default.account-type}")
    private String defaultAccountType;


    public CreateAccountResponse createDemandDepositAccount(String userKey) {
        String apiName = "createDemandDepositAccount";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        CreateAccountRequest request = CreateAccountRequest.of(header, defaultAccountType);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/createDemandDepositAccount", request, CreateAccountResponse.class);
    }

    public InquireAccountListResponse inquireDemandDepositAccountList(String userKey) {
        String apiName = "inquireDemandDepositAccountList";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        InquireAccountListRequest request = InquireAccountListRequest.of(header);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/inquireDemandDepositAccountList", request, InquireAccountListResponse.class);
    }

    public InquireAccountHistoryResponse inquireTransactionHistory(String userKey, String accountNo, Long transactionNo) {
        String apiName = "inquireTransactionHistory";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        InquireAccountHistoryRequest request = InquireAccountHistoryRequest.of(header, accountNo, transactionNo);
        return webClientHelper.postRequest(url + "/api/v1/edu/demandDeposit/inquireTransactionHistory", request, InquireAccountHistoryResponse.class);
    }
}
