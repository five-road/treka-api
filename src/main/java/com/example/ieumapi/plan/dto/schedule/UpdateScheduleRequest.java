package com.example.ieumapi.plan.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "일정 수정 요청(부분 수정)")
public class UpdateScheduleRequest {

    @Schema(description = "일정 제목", example = "부산 이동(변경)")
    private String title;

    @Schema(description = "설명", example = "KTX 11:00로 변경")
    private String description;

    @Schema(description = "시작 시각", example = "2025-07-12T11:00:00")
    private LocalDateTime startAt;

    @Schema(description = "종료 시각", example = "2025-07-12T13:00:00")
    private LocalDateTime endAt;

    @Schema(description = "장소명", example = "서울역 2층")
    private String placeName;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "위도")
    private Double lat;

    @Schema(description = "경도")
    private Double lng;
}
