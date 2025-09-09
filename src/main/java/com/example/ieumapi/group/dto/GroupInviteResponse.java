package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "그룹 초대 응답 DTO")
public class GroupInviteResponse {
    @Schema(description = "초대 ID", example = "100")
    private final Long inviteId;

    @Schema(description = "그룹 ID", example = "10")
    private final Long groupId;

    @Schema(description = "초대 코드", example = "AbCd1234Ef")
    private final String inviteCode;

    @Schema(description = "초대 생성 시각", example = "2025-07-15T12:00:00")
    private final LocalDateTime createdAt;

    @Schema(description = "초대 만료 시각", example = "2025-07-22T12:00:00")
    private final LocalDateTime expiresAt;

    @Schema(description = "초대 상태", example = "PENDING")
    private final String status;
}