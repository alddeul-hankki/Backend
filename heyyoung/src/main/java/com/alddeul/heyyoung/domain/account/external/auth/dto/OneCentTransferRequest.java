package com.alddeul.heyyoung.domain.account.external.auth.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OneCentTransferRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountNo;
    private String authText;

    public static OneCentTransferRequest of(RequestHeader header, String accountNo, String authText) {
        return OneCentTransferRequest.builder()
                .header(header)
                .accountNo(accountNo)
                .authText(authText)
                .build();

    }
}
