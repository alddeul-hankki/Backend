package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
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
