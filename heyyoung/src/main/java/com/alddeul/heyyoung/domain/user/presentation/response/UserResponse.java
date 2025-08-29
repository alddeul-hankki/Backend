package com.alddeul.heyyoung.domain.user.presentation.response;

public record UserResponse(
        Status status,
        String message,
        Long userId,
        String email
) {
    public static UserResponse of(Status status, String message) {
        return new UserResponse(status, message, null, null);
    }

    public static UserResponse of(Status status, Long userId, String email) {
        return new UserResponse(status, null, userId, email);
    }

    public enum Status {
        REGISTRATION_FAILED,
        REGISTERED,
        ALREADY_REGISTERED,
    }
}
