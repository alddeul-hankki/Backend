package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InquireAccountHistoryListResponse(
        @JsonProperty("Header") RequestHeader header,
        @JsonProperty("REC") Rec rec
) {
    public record Rec(
            String totalCount,
            List<Detail> list
    ) {
        public record Detail(
                Long transactionUniqueNo,
                String transactionDate,
                String transactionTime,
                String transactionType,
                String transactionTypeName,
                String transactionAccountNo,
                Long transactionBalance,
                Long transactionAfterBalance,
                String transactionSummary,
                String transactionMemo
        ) {}
    }
}