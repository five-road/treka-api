package com.example.ieumapi.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "친구 요청 생성 응답 DTO")
public class FriendRequestResponse {
    @Schema(description = "친구 요청 ID", example = "789")
    private final Long requestId;

    @Schema(description = "요청 보낸 사용자 ID", example = "123")
    private final Long fromUserId;

    @Schema(description = "요청 받은 사용자 ID", example = "456")
    private final Long toUserId;

    @Schema(description = "요청 상태", example = "PENDING")
    private final String status;

    @Schema(description = "생성 시각", example = "2025-07-11T15:23:45")
    private final LocalDateTime createdAt;
}
