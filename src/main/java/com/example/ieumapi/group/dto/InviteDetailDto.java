package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "초대 상세 정보 DTO")
public class InviteDetailDto {
    @Schema(description = "초대 ID", example = "100")
    private final Long inviteId;

    @Schema(description = "그룹 ID", example = "10")
    private final Long groupId;

    @Schema(description = "그룹 이름", example = "여행 모임")
    private final String groupName;

    @Schema(description = "그룹 설명", example = "여행 설명")
    private final String groupDescription;

    @Schema(description = "초대 보낸 사용자 ID", example = "123")
    private final Long fromUserId;

    @Schema(description = "초대 받은 사용자 ID (null인 경우 링크 초대)", nullable = true)
    private final Long toUserId;

    @Schema(description = "초대 코드", example = "AbCd1234Ef")
    private final String inviteCode;

    @Schema(description = "상태", example = "PENDING")
    private final String status;

    @Schema(description = "생성 시각", example = "2025-07-15T12:00:00")
    private final LocalDateTime createdAt;

    @Schema(description = "만료 시각", example = "2025-07-22T12:00:00")
    private final LocalDateTime expiresAt;
}
