package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Schema(description = "그룹 상세 정보 응답 DTO")
public class GroupDetailResponse {
    @Schema(description = "그룹 ID", example = "1001")
    private final Long groupId;

    @Schema(description = "그룹 이름", example = "여행 모임")
    private final String name;

    @Schema(description = "생성자 ID", example = "123")
    private final Long ownerId;

    @Schema(description = "생성 시각", example = "2025-07-12T10:00:00")
    private final LocalDateTime createdAt;

    @Schema(description = "그룹 멤버 목록")
    private final List<MemberDto> members;

    @Schema(description = "그룹 초대 목록")
    private final List<InviteDto> invites;
}
