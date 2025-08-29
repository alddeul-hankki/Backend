package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InquireAccountBalanceRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountNo;

    public static InquireAccountBalanceRequest of(RequestHeader header, String accountNo) {
        return InquireAccountBalanceRequest.builder()
                .header(header)
                .accountNo(accountNo)
                .build();
    }
}
