package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InquireAccountHistoryListRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountNo;
    private String startDate;
    private String endDate;
    private String transactionType;
    private String orderByType;

    public static InquireAccountHistoryListRequest of(RequestHeader header, String accountNo, String startDate, String endDate, String transactionType) {
        return InquireAccountHistoryListRequest.builder()
                .header(header)
                .accountNo(accountNo)
                .startDate(startDate)
                .endDate(endDate)
                .transactionType(transactionType)
                .orderByType("DESC")
                .build();
    }
}
