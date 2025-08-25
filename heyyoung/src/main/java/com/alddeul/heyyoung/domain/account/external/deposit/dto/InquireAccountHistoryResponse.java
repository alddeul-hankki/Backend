package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquireAccountHistoryResponse {
    @JsonProperty("Header")
    private RequestHeader header;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private Long transactionUniqueNo;
        private String transactionDate;
        private String transactionTime;
        private String transactionType;
        private String transactionTypeName;
        private String transactionAccountNo;
        private Long transactionBalance;
        private Long transactionAfterBalance;
        private String transactionSummary;
        private String transactionMemo;
    }
}