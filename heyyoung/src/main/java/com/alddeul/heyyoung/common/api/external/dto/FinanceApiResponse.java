package com.alddeul.heyyoung.common.api.external.dto;

public record FinanceApiResponse<T>(boolean success, T data, ErrorResponse error) {

    public static <T> FinanceApiResponse<T> ofData(T data) {
        return new FinanceApiResponse<>(true, data, null);
    }

    public static <T> FinanceApiResponse<T> ofError(ErrorResponse error) {
        return new FinanceApiResponse<>(false,null, error);
    }
}