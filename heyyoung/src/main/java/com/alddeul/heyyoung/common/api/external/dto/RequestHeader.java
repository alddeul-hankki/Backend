package com.alddeul.heyyoung.common.api.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Builder
@AllArgsConstructor
public class RequestHeader {
    private String apiName;
    private String transmissionDate;
    private String transmissionTime;
    private String institutionCode;
    private String fintechAppNo;
    private String apiServiceCode;
    private String institutionTransactionUniqueNo;
    private String apiKey;
    private String userKey;

    private static final String DEFAULT_INSTITUTION_CODE = "00100";
    private static final String DEFAULT_FINTECH_APP_NO = "001";

    public static RequestHeader of(String apiName, String apiKey, String userKey) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String uniqueNo = generateUniqueNo(date, time);

        return RequestHeader.builder()
                .apiName(apiName)
                .transmissionDate(date)
                .transmissionTime(time)
                .institutionCode(DEFAULT_INSTITUTION_CODE)
                .fintechAppNo(DEFAULT_FINTECH_APP_NO)
                .apiServiceCode(apiName)
                .institutionTransactionUniqueNo(uniqueNo)
                .apiKey(apiKey)
                .userKey(userKey)
                .build();
    }

    private static String generateUniqueNo(String date, String time) {
        int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return date + time + random;
    }
}
