package com.example.ieumapi.plan.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum PlanError implements CustomError {
    PLAN_NOT_FOUND("PLAN_NOT_FOUND", "여행 계획을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    GROUP_NOT_FOUND("GROUP_NOT_FOUND", "그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("FORBIDDEN", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_DATE_RANGE("INVALID_DATE_RANGE", "시작일은 종료일보다 앞서야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_TITLE("INVALID_TITLE", "제목은 필수이며 공백일 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    PlanError(String code, String message, HttpStatus status) {
        this.code = code; this.message = message; this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
