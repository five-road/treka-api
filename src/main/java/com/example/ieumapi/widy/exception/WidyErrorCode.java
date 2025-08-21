package com.example.ieumapi.widy.exception;

import com.example.ieumapi.global.exception.CustomError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum WidyErrorCode implements CustomError {
    WIDY_NOT_FOUND("WIDY_001", "위디를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    GROUP_ID_REQUIRED("WIDY_002", "GROUP scope에는 Group ID가 필수입니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("WIDY_003", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    WidyErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
