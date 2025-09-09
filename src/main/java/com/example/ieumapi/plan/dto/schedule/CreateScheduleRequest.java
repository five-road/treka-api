package com.example.ieumapi.plan.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "일정 생성 요청")
public class CreateScheduleRequest {

    @NotBlank(message = "title은 필수입니다.")
    @Schema(description = "일정 제목", example = "부산 이동")
    private String title;

    @Schema(description = "설명", example = "KTX 10:30 출발")
    private String description;

    @NotNull(message = "startAt은 필수입니다.")
    @Schema(description = "시작 시각", example = "2025-07-12T10:30:00")
    private LocalDateTime startAt;

    @NotNull(message = "endAt은 필수입니다.")
    @Schema(description = "종료 시각", example = "2025-07-12T12:30:00")
    private LocalDateTime endAt;

    @Schema(description = "장소명", example = "서울역")
    private String placeName;

    @Schema(description = "주소", example = "서울 중구 한강대로 405")
    private String address;

    @Schema(description = "위도", example = "37.5551")
    private Double lat;

    @Schema(description = "경도", example = "126.9707")
    private Double lng;
}
