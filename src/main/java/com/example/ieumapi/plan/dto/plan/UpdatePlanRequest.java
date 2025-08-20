package com.example.ieumapi.plan.dto.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "플랜 수정 요청(부분 수정)")
public class UpdatePlanRequest {

    @Schema(description = "플랜 제목", example = "부산 3박4일(변경)")
    private String title;

    @Schema(description = "설명", example = "스케줄 여유 추가")
    private String description;

    @Schema(description = "시작일", example = "2025-07-11")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2025-07-15")
    private LocalDate endDate;

    @Schema(description = "지역명", example = "부산 해운대")
    private String locationName;
}
