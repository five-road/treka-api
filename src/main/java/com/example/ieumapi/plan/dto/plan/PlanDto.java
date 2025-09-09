package com.example.ieumapi.plan.dto.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "플랜 DTO")
public class PlanDto {
    @Schema(description = "플랜 ID", example = "1001")
    private final Long planId;

    @Schema(description = "소유자 ID", example = "123")
    private final Long ownerId;

    @Schema(description = "그룹 ID", nullable = true, example = "10")
    private final Long groupId;

    @Schema(description = "제목")
    private final String title;

    @Schema(description = "설명")
    private final String description;

    @Schema(description = "시작일")
    private final LocalDate startDate;

    @Schema(description = "종료일")
    private final LocalDate endDate;

    @Schema(description = "지역명", nullable = true)
    private final String locationName;

    @Schema(description = "생성 시각")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시각")
    private final LocalDateTime updatedAt;
}
