package com.alddeul.solsolhanhankki.account.deposit.client.dto;

import com.alddeul.solsolhanhankki.global.client.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 출금
 * 돈의 흐름: SSAFY Account -> PayMoney(기타 외부 자산)
 */
@Getter
@Builder
@AllArgsConstructor
public class WithdrawalAccountRequest {
    @JsonProperty("Header")
    private RequestHeader header;
    private String accountNo;
    private Long transactionBalance;
    private String transactionSummary;

    public static WithdrawalAccountRequest of(RequestHeader header, String accountNo, Long transactionBalance, String transactionSummary) {
        return WithdrawalAccountRequest.builder()
                .header(header)
                .accountNo(accountNo)
                .transactionBalance(transactionBalance)
                .transactionSummary(transactionSummary)
                .build();
    }
}
