package com.example.ieumapi.file;

import com.example.ieumapi.global.exception.CustomBaseException;
import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public class FileStorageException extends CustomBaseException {

    public FileStorageException(FileStorageError error) {
        super(error);
    }

    public enum FileStorageError implements CustomError {
        UPLOAD_FAILED("FILE_STORAGE_001", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

        private final String code;
        private final String message;
        private final HttpStatus status;

        FileStorageError(String code, String message, HttpStatus status) {
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
