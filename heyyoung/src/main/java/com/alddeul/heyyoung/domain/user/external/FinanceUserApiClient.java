package com.alddeul.heyyoung.domain.user.external;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.common.api.external.utils.WebClientHelper;
import com.alddeul.heyyoung.domain.user.external.dto.FinanceUserRequest;
import com.alddeul.heyyoung.domain.user.external.dto.FinanceUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinanceUserApiClient {
    private final WebClientHelper webClientHelper;

    @Value("${financial.api.base-url}")
    private String url;

    @Value("${financial.api.key}")
    private String appKey;

    public FinanceApiResponse<FinanceUserResponse> loginUser(String email) {
        FinanceUserRequest request = FinanceUserRequest.of(appKey, email);
        return webClientHelper.postRequest(url + "/api/v1/member", request, FinanceUserResponse.class);
    }

    public FinanceApiResponse<FinanceUserResponse> searchUser(String email) {
        FinanceUserRequest request = FinanceUserRequest.of(appKey, email);
        return webClientHelper.postRequest(url + "/api/v1/member/search", request, FinanceUserResponse.class);
    }
}
