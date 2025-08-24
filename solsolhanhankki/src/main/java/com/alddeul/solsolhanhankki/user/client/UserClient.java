package com.alddeul.solsolhanhankki.user.client;

import com.alddeul.solsolhanhankki.global.client.util.WebClientHelper;
import com.alddeul.solsolhanhankki.user.client.dto.UserApiRequest;
import com.alddeul.solsolhanhankki.user.client.dto.UserApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserClient {
    private final WebClientHelper webClientHelper;

    @Value("${financial.api.base-url}")
    private String url;

    @Value("${financial.api.key}")
    private String appKey;

    public UserApiResponse loginUser(String email) {
        UserApiRequest request = UserApiRequest.of(appKey, email);
        return webClientHelper.postRequest(url + "/api/v1/member", request, UserApiResponse.class);
    }

    public UserApiResponse searchUser(String email) {
        UserApiRequest request = UserApiRequest.of(appKey, email);
        return webClientHelper.postRequest(url + "/api/v1/member/search", request, UserApiResponse.class);
    }
}
