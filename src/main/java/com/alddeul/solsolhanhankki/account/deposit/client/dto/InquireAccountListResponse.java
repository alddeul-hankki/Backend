package com.alddeul.solsolhanhankki.account.deposit.client.dto;

import com.alddeul.solsolhanhankki.global.client.dto.ResponseHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InquireAccountListResponse {
    @JsonProperty("Header")
    private ResponseHeader header;
    @JsonProperty("REC")
    private List<Rec> rec;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private String bankCode;
        private String bankName;
        private String userName;
        private String accountNo;
        private String accountName;
        private String accountTypeCode;
        private String accountTypeName;
        private String accountCreatedDate;
        private String accountExpiryDate;
        private String dailyTransferLimit;
        private String oneTimeTransferLimit;
        private String accountBalance;
        private String lastTransactionDate;
        private String current;
    }
}
