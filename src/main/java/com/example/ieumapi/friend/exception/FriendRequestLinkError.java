package com.example.ieumapi.friend.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum FriendRequestLinkError implements CustomError {
    UNAUTHORIZED("UNAUTHORIZED", "로그인이 필요합니다.",HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    INVALID_EXPIRES_AT("INVALID_EXPIRES_AT", "유효하지 않은 만료 시각입니다.",HttpStatus.BAD_REQUEST),
    INVITE_NOT_FOUND("INVITE_NOT_FOUND", "초대 링크를 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    INVITE_ALREADY_PROCESSED("INVITE_ALREADY_PROCESSED", "이미 처리된 초대 링크입니다.",HttpStatus.BAD_REQUEST),
    INVITE_EXPIRED("INVITE_EXPIRED", "초대 링크가 만료되었습니다.",HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    FriendRequestLinkError(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
