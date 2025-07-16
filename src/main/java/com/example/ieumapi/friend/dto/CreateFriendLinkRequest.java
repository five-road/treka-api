package com.example.ieumapi.friend.dto;

import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record CreateFriendLinkRequest(
        @Future(message = "expiresAt는 미래 시각이어야 합니다.")
        LocalDateTime expiresAt
) {}
