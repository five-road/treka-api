package com.example.ieumapi.friend.exception;

import com.example.ieumapi.global.exception.CustomError;
import org.springframework.http.HttpStatus;

public enum FriendRequestError implements CustomError {
    REQUEST_NOT_PENDING("REQUEST_NOT_PENDING", "요청이 대기 중이 아닙니다.", HttpStatus.BAD_REQUEST),
    REQUEST_NOT_FOUND("REQUEST_NOT_FOUND", "해당 친구 요청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("FORBIDDEN", "해당 요청을 수락할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_TO_USER_ID("INVALID_TO_USER_ID", "toUserId는 필수이며, 양수여야 합니다.", HttpStatus.BAD_REQUEST),
    SELF_REQUEST_NOT_ALLOWED("SELF_REQUEST_NOT_ALLOWED", "자기 자신에게 친구 요청을 보낼 수 없습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "toUserId에 해당하는 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_FRIEND("ALREADY_FRIEND", "이미 친구 관계가 존재합니다.", HttpStatus.BAD_REQUEST),
    REQUEST_ALREADY_EXISTS("REQUEST_ALREADY_EXISTS", "이미 보낸 요청이 대기 중입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    FriendRequestError(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
