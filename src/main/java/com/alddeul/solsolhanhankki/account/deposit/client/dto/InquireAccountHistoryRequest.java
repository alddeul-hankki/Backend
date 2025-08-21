package com.alddeul.solsolhanhankki.account.deposit.client.dto;

import com.alddeul.solsolhanhankki.global.client.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InquireAccountHistoryRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountNo;
    private Long transactionUniqueNo;

    public static InquireAccountHistoryRequest of(RequestHeader header, String accountNo, Long transactionUniqueNo) {
        return InquireAccountHistoryRequest.builder()
                .header(header)
                .accountNo(accountNo)
                .transactionUniqueNo(transactionUniqueNo)
                .build();
    }
}
