package com.alddeul.solsolhanhankki.account.deposit.client.dto;

import com.alddeul.solsolhanhankki.global.client.dto.RequestHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InquireAccountListRequest {
    @JsonProperty("Header")
    private RequestHeader header;

    public static InquireAccountListRequest of(RequestHeader header) {
        return InquireAccountListRequest.builder().header(header).build();
    }
}
