package com.alddeul.solsolhanhankki.user.client;

import com.alddeul.solsolhanhankki.global.client.util.WebClientHelper;
import com.alddeul.solsolhanhankki.user.client.dto.UserRequest;
import com.alddeul.solsolhanhankki.user.client.dto.UserResponse;
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

    public UserResponse loginUser(String email) {
        UserRequest request = UserRequest.of(appKey, email);
        return webClientHelper.postRequest(url + "/api/v1/member", request, UserResponse.class);
    }

    public UserResponse searchUser(String email) {
        UserRequest request = UserRequest.of(appKey, email);
        return webClientHelper.postRequest(url + "/api/v1/member/search", request, UserResponse.class);
    }
}
