package com.example.ieumapi.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "공통 에러 응답")
public class ErrorResponse {
    @Schema(description = "응답 성공 여부", example = "false")
    private final boolean success = false;

    @Schema(description = "에러 코드", example = "USER_DUPLICATED")
    private final String errorCode;

    @Schema(description = "에러 메시지", example = "이미 가입된 이메일입니다.")
    private final String message;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}

