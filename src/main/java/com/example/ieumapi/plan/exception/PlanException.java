package com.example.ieumapi.plan.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class PlanException extends CustomBaseException {
    public PlanException(PlanError error) { super(error); }
}
