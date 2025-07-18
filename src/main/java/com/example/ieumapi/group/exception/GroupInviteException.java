package com.example.ieumapi.group.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class GroupInviteException extends CustomBaseException {
    public GroupInviteException(GroupInviteError error) {
        super(error);
    }
}
