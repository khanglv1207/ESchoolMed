package com.swp391.eschoolmed.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.OK),
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED), //ko co quyen truy cap
    UNAUTHORIZED(1002, "Unauthorized", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1003, "Invalid key", HttpStatus.BAD_REQUEST), //sai khoa
    USER_NOT_FOUND(1004, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_OR_PASSWORD_ERROR(1005, "Username or password error", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
