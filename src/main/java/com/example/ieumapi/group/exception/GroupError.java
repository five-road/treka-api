package com.example.ieumapi.group.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum GroupError implements CustomError {
    NOT_FOUND("NOT_FOUND", "그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("FORBIDDEN", "그룹 멤버만 접근할 수 있습니다.", HttpStatus.FORBIDDEN),
    INVALID_NAME("INVALID_NAME", "그룹 이름은 필수이며 공백일 수 없습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "해당 그룹 멤버를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_ROLE("INVALID_ROLE", "유효하지 않은 역할입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    GroupError(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
