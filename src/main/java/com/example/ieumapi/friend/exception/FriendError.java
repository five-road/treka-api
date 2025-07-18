package com.example.ieumapi.friend.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum FriendError implements CustomError {
    INVALID_USER_ID("INVALID_USER_ID", "userId는 양수여야 합니다.", HttpStatus.BAD_REQUEST),
    SELF_UNFRIEND_NOT_ALLOWED("SELF_UNFRIEND_NOT_ALLOWED", "자기 자신을 친구에서 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
    FRIEND_NOT_FOUND("FRIEND_NOT_FOUND", "해당 친구 관계를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("FORBIDDEN", "친구 관계를 삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    FriendError(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
