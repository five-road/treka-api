package com.example.ieumapi.friend.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class FriendException extends CustomBaseException {
    public FriendException(FriendError error) {
        super(error);
    }
}
