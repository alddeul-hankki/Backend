package com.alddeul.solsolhanhankki.global.client.dto;

public record ApiResponse<T>(boolean success, T data, ErrorResponse error) {

    public static <T> ApiResponse<T> ofData(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> ofError(ErrorResponse error) {
        return new ApiResponse<>(false,null, error);
    }
}