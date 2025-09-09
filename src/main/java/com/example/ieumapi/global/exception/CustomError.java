package com.example.ieumapi.global.exception;

import org.springframework.http.HttpStatus;

public interface CustomError {
    String getCode();
    String getMessage();
    HttpStatus getStatus();
}

