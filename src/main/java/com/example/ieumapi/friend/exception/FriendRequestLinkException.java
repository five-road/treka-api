package com.example.ieumapi.friend.exception;

import com.example.ieumapi.global.exception.CustomBaseException;

public class FriendRequestLinkException extends CustomBaseException {
    public FriendRequestLinkException(FriendRequestLinkError error) {
        super(error);
    }
}

