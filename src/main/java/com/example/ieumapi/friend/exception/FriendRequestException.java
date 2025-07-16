package com.example.ieumapi.friend.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class FriendRequestException extends CustomBaseException {
    public FriendRequestException(FriendRequestError error) {
        super(error);
    }
}
