package com.alddeul.heyyoung.common.api.external.dto;

public enum ErrorCode {
    E4002("이미 존재하는 회원입니다"),
    E4003("존재하지 않는 회원입니다"),
    H1009("USER_KEY가 유효하지 않습니다"),
    E4003("존재하지 않는 회원입니다"),

    // 기관거래고유번호가 중복된 값 -> 이미 처리된 요청으로 신뢰
    H1007("이미 거래된 요청입니다."),

    A1014("계좌 잔액이 부족하여 거래가 실패했습니다."),
    A1003("계좌번호가 유효하지 않습니다."),
    A1011("거래 금액이 유효하지 않습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
