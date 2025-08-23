package com.alddeul.solsolhanhankki.account.deposit.client.dto;

import com.alddeul.solsolhanhankki.global.client.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateAccountRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountTypeUniqueNo;

    public static CreateAccountRequest of(RequestHeader header, String accountTypeUniqueNo) {
        return CreateAccountRequest.builder()
                .header(header)
                .accountTypeUniqueNo(accountTypeUniqueNo)
                .build();
    }
}
