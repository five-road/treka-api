package com.example.ieumapi.user.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements CustomError {
    USER_DUPLICATED("USER_DUPLICATED", "이미 가입된 이메일입니다.", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CURSOR("INVALID_CURSOR", "cursor 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getMessage() { return message; }
    @Override
    public HttpStatus getStatus() { return status; }
}
