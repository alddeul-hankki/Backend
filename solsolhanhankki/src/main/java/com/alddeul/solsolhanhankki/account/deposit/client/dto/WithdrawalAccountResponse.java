package com.alddeul.solsolhanhankki.account.deposit.client.dto;

import com.alddeul.solsolhanhankki.global.client.dto.ResponseHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class WithdrawalAccountResponse {
    @JsonProperty("Header")
    private ResponseHeader header;
    @JsonProperty("REC")
    private List<WithdrawalAccountResponse.Rec> rec;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private String transactionUniqueNo;
        private String transactionDate;
    }
}
