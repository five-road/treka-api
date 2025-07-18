package com.example.ieumapi.friend.dto;

import com.example.ieumapi.friend.domain.FriendLinkStatus;

import java.time.LocalDateTime;

public record AcceptFriendLinkResponse(
        String inviteCode,
        FriendLinkStatus status,
        Long fromUserId,
        Long toUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}