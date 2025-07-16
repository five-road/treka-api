package com.example.ieumapi.group.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum GroupInviteError implements CustomError {
    GROUP_NOT_FOUND("GROUP_NOT_FOUND", "그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("FORBIDDEN", "초대 권한이 없습니다.", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("USER_NOT_FOUND", "초대 대상 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVITE_NOT_FOUND("INVITE_NOT_FOUND", "유효하지 않은 초대 코드입니다.", HttpStatus.NOT_FOUND),
    INVITE_EXPIRED("INVITE_EXPIRED", "만료된 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    INVITE_NOT_PENDING("INVITE_NOT_PENDING", "이미 처리된 초대입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    GroupInviteError(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
