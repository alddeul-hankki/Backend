package com.alddeul.heyyoung.common.api.external.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseHeader {
    private String responseCode;
    private String responseMessage;
    private String apiName;
    private String transmissionDate;
    private String transmissionTime;
    private String institutionCode;
    private String apiKey;
    private String apiServiceCode;
    private String institutionTransactionUniqueNo;
}
