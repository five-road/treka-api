package com.example.ieumapi.widy.exception;

import com.example.ieumapi.global.exception.CustomBaseException;
import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public class WidyException extends CustomBaseException {

    public WidyException(WidyError error) {
        super(error);
    }

    public enum WidyError implements CustomError {
        WIDY_NOT_FOUND("WIDY_001", "Widy를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
        INVALID_EMOTION("WIDY_002", "유효하지 않은 감정 값입니다.", HttpStatus.BAD_REQUEST),
        IMAGE_UPLOAD_FAILED("WIDY_003", "이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

        private final String code;
        private final String message;
        private final HttpStatus status;

        WidyError(String code, String message, HttpStatus status) {
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
}
