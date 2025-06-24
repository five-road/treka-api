package com.example.ieumapi.global.exception;


import lombok.Getter;

@Getter
public abstract class CustomBaseException extends RuntimeException {
    private final CustomError error;

    public CustomBaseException(CustomError error) {
        super(error.getMessage());
        this.error = error;
    }

}

