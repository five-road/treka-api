package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "초대 거절 응답 DTO")
public class DeclineInviteResponse {
    @Schema(description = "초대 ID", example = "100")
    private final Long inviteId;

    @Schema(description = "그룹 ID", example = "10")
    private final Long groupId;

    @Schema(description = "사용자 ID (거절한 멤버)", example = "456")
    private final Long userId;

    @Schema(description = "거절 시각", example = "2025-07-16T14:00:00")
    private final LocalDateTime declinedAt;
}

