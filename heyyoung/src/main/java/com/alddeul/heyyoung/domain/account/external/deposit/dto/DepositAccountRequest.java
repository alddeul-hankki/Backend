package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 입금
 * 돈의 흐름: PayMoney(기타 외부 자산) -> SSAFY Account
 */
@Getter
@Builder
@AllArgsConstructor
public class DepositAccountRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountNo;
    private Long transactionBalance;
    private String transactionSummary;

    public static DepositAccountRequest of(RequestHeader header, String accountNo, Long transactionBalance, String transactionSummary) {
        return DepositAccountRequest.builder()
                .header(header)
                .accountNo(accountNo)
                .transactionBalance(transactionBalance)
                .transactionSummary(transactionSummary)
                .build();
    }
}