package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "그룹 정보 DTO")
public class GroupDto {
    @Schema(description = "그룹 ID", example = "1001")
    private final Long groupId;

    @Schema(description = "그룹 이름", example = "여행 모임")
    private final String name;

    @Schema(description = "그룹 설명", example = "같이 여행 계획 세우는 모임")
    private final String description;

    @Schema(description = "그룹 생성자 사용자 ID", example = "123")
    private final Long ownerId;

    @Schema(description = "그룹 생성 시각", example = "2025-07-12T10:00:00")
    private final LocalDateTime createdAt;

    @Schema(description = "내가 그룹에 가입한 시각 (조회용)", example = "2025-07-13T11:30:00", nullable = true)
    private final LocalDateTime joinedAt;

    @Schema(description = "활성 링크 초대 코드(있을 경우)", example = "AbCd1234Ef", nullable = true)
    private final String inviteCode;

    @Schema(description = "그룹 멤버 수", example = "5")
    private final long memberCount;
}
