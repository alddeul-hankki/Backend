package com.alddeul.heyyoung.common.api.external.dto;

public enum ErrorCode {
    E4003("존재하지 않는 회원입니다");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
