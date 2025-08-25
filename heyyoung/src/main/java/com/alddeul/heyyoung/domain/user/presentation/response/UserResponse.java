package com.alddeul.heyyoung.domain.user.presentation.response;

public record UserResponse(
        Status status,
        String message

) {
    public static UserResponse of(Status status, String message) {
        return new UserResponse(status, message);
    }

    public static UserResponse of(Status status) {
        return new UserResponse(status, null);
    }

    public enum Status {
        REGISTRATION_FAILED,
        REGISTERED,
        ALREADY_REGISTERED,
    }
}
