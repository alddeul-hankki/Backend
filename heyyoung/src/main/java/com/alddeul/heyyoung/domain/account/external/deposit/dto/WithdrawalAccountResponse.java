package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.ResponseHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class WithdrawalAccountResponse {
    @JsonProperty("Header")
    private ResponseHeader header;
    @JsonProperty("REC")
    private List<Rec> rec;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private Long transactionUniqueNo;
        private String transactionDate;
    }
}