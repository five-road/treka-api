package com.example.ieumapi.plan.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class PlaceException extends CustomBaseException {
    public PlaceException(PlaceError error) { super(error); }
}
