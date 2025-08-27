package com.alddeul.heyyoung.common.api.external.dto;

public enum ErrorCode {
    E4002("이미 존재하는 회원입니다"),
    E4003("존재하지 않는 회원입니다"),
    H1009("USER_KEY가 유효하지 않습니다")
    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
