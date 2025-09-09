package com.example.ieumapi.group.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class GroupException extends CustomBaseException {
    public GroupException(GroupError error) {
        super(error);
    }
}