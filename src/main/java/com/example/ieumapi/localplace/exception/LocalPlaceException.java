package com.example.ieumapi.localplace.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class LocalPlaceException extends CustomBaseException {

    public LocalPlaceException(LocalPlaceErrorCode localPlaceErrorCode) {
        super(localPlaceErrorCode);
    }
}