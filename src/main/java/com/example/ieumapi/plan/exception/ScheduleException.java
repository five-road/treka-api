package com.example.ieumapi.plan.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class ScheduleException extends CustomBaseException {
    public ScheduleException(ScheduleError error) { super(error); }
}
