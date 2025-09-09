package com.example.ieumapi.friend.dto;

import com.example.ieumapi.friend.domain.FriendLinkStatus;

import java.time.LocalDateTime;

public record FriendLinkInfoResponse(
        String inviteCode,
        FriendLinkStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        Long fromUserId,
        Long toUserId,
        String toUserName,
        String toUserNickName,
        String toUserProfileImage
) {}
