package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquireAccountHistoryListResponse {
    @JsonProperty("Header")
    private RequestHeader header;
    @JsonProperty("REC")
    private Rec rec;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private String totalCount;
        private Detail list;

        @Getter
        @NoArgsConstructor
        private static class Detail {
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
}