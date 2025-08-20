package com.example.ieumapi.plan.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum NoteError implements CustomError {
    PLAN_NOT_FOUND("PLAN_NOT_FOUND", "여행 계획을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOTE_NOT_FOUND("NOTE_NOT_FOUND", "메모를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("FORBIDDEN", "권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    NoteError(String code, String message, HttpStatus status) {
        this.code = code; this.message = message; this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
