package com.example.ieumapi.user.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class UserException extends CustomBaseException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}

