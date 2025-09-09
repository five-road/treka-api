package com.example.ieumapi.plan.dto.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "플랜 생성 요청")
public class CreatePlanRequest {

    @NotBlank(message = "title은 필수입니다.")
    @Schema(description = "플랜 제목", example = "부산 2박3일")
    private String title;

    @Schema(description = "설명", example = "해운대 숙소, 온천, 회식 예정")
    private String description;

    @NotNull(message = "startDate는 필수입니다.")
    @Schema(description = "시작일", example = "2025-07-12")
    private LocalDate startDate;

    @NotNull(message = "endDate는 필수입니다.")
    @Schema(description = "종료일", example = "2025-07-14")
    private LocalDate endDate;

    @Schema(description = "지역명", example = "부산")
    private String locationName;

    @Schema(description = "그룹 ID(그룹 플랜 생성 시 지정). null이면 개인 플랜", example = "10", nullable = true)
    private Long groupId;
}
