package com.alddeul.heyyoung.domain.account.external.auth.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckAuthCodeResponse {
    @JsonProperty("Header")
    private RequestHeader header;
    @JsonProperty("REC")
    private Rec rec;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private String status;
        private Long transactionUniqueNo;
        private String accountNo;

    }
}
