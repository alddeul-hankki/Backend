package com.alddeul.heyyoung.domain.account.external.auth.dto;

import com.alddeul.heyyoung.common.api.external.dto.ResponseHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class OneCentTransferResponse {
    @JsonProperty("Header")
    private ResponseHeader header;
    @JsonProperty("REC")
    private Rec rec;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private Long transactionUniqueNo;
        private String accountNo;
    }
}
