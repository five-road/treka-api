package com.example.ieumapi.widy.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class WidyException extends CustomBaseException {

    public WidyException(WidyErrorCode widyErrorCode) {
        super(widyErrorCode);
    }
}