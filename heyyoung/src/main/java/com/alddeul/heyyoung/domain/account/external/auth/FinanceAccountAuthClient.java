package com.alddeul.heyyoung.domain.account.external.auth;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.alddeul.heyyoung.common.api.external.utils.WebClientHelper;
import com.alddeul.heyyoung.domain.account.external.auth.dto.CheckAuthCodeRequest;
import com.alddeul.heyyoung.domain.account.external.auth.dto.CheckAuthCodeResponse;
import com.alddeul.heyyoung.domain.account.external.auth.dto.OneCentTransferRequest;
import com.alddeul.heyyoung.domain.account.external.auth.dto.OneCentTransferResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinanceAccountAuthClient {
    private final WebClientHelper webClientHelper;

    @Value("${financial.api.base-url}")
    private String url;

    @Value("${financial.api.key}")
    private String appKey;

    @Value("${financial.api.name}")
    private String appName;

    public FinanceApiResponse<OneCentTransferResponse> openAccountAuth(String userKey, String accountNo) {
        String apiName = "openAccountAuth";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        OneCentTransferRequest request = OneCentTransferRequest.of(header, accountNo, appName);
        return webClientHelper.postRequest(url + "/api/v1/edu/accountAuth/openAccountAuth", request, OneCentTransferResponse.class);
    }

    public FinanceApiResponse<CheckAuthCodeResponse> checkAuthCode(String userKey, String accountNo, String authCode) {
        String apiName = "checkAuthCode";
        RequestHeader header = RequestHeader.of(apiName, appKey, userKey);
        CheckAuthCodeRequest request = CheckAuthCodeRequest.of(header, accountNo, appName, authCode);
        return webClientHelper.postRequest(url + "/api/v1/edu/accountAuth/checkAuthCode", request, CheckAuthCodeResponse.class);
    }
}
