package com.example.ieumapi.plan.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "일정 DTO")
public class ScheduleDto {
    @Schema(description = "일정 ID", example = "301")
    private final Long scheduleId;

    @Schema(description = "플랜 ID", example = "1001")
    private final Long planId;

    @Schema(description = "제목")
    private final String title;

    @Schema(description = "설명")
    private final String description;

    @Schema(description = "시작 시각")
    private final LocalDateTime startAt;

    @Schema(description = "종료 시각")
    private final LocalDateTime endAt;

    @Schema(description = "장소명", nullable = true)
    private final String placeName;

    @Schema(description = "주소", nullable = true)
    private final String address;

    @Schema(description = "위도", nullable = true)
    private final Double lat;

    @Schema(description = "경도", nullable = true)
    private final Double lng;

    @Schema(description = "생성 시각")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시각")
    private final LocalDateTime updatedAt;
}
