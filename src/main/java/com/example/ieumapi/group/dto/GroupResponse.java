package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "그룹 정보 응답 DTO")
public class GroupResponse {
    @Schema(description = "그룹 ID", example = "1001")
    private final Long groupId;

    @Schema(description = "그룹 이름", example = "여행 모임")
    private final String name;

    @Schema(description = "그룹 설명", example = "여행 설명")
    private final String description;

    @Schema(description = "그룹 생성자 사용자 ID", example = "123")
    private final Long ownerId;

    @Schema(description = "생성 시각", example = "2025-07-12T10:00:00")
    private final LocalDateTime createdAt;
}
