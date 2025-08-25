package com.alddeul.heyyoung.domain.account.external.auth.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CheckAuthCodeRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountNo;
    private String authText;
    private String authCode;

    public static CheckAuthCodeRequest of(RequestHeader header, String accountNo, String authText, String authCode) {
        return CheckAuthCodeRequest.builder()
                .header(header)
                .accountNo(accountNo)
                .authText(authText)
                .authCode(authCode)
                .build();
    }
}
