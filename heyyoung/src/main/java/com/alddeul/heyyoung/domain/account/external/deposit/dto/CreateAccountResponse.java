package com.alddeul.heyyoung.domain.account.external.deposit.dto;

import com.alddeul.heyyoung.common.api.external.dto.ResponseHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Getter
@NoArgsConstructor
public class CreateAccountResponse {
    @JsonProperty("Header")
    private ResponseHeader header;
    @JsonProperty("REC")
    private Rec rec;
    private Currency currency;

    @Getter
    @NoArgsConstructor
    public static class Rec {
        private String bankCode;
        private String accountNo;
        private Currency currency;

        @Getter
        @NoArgsConstructor
        public static class Currency {
            private String currency;
            private String currencyName;
        }
    }
}
