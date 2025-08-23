package com.alddeul.solsolhanhankki.account.auth.client.dto;

import com.alddeul.solsolhanhankki.global.client.dto.RequestHeader;
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
