package com.example.ieumapi.localplace.exception;

import com.example.ieumapi.global.exception.CustomError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LocalPlaceErrorCode implements CustomError {
    LOCAL_PLACE_NOT_FOUND("LOCAL_PLACE_001", "장소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("LOCAL_PLACE_002", "이 장소를 수정/삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    LocalPlaceErrorCode(String code, String message, HttpStatus status) {
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
