package com.example.ieumapi.plan.dto.place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "위치 정보 DTO")
public class PlaceDto {
    @Schema(description = "장소 ID", example = "101")
    private final Long placeId;

    @Schema(description = "플랜 ID", example = "1")
    private final Long planId;

    @Schema(description = "이름", example = "스타벅스 홍대입구역점")
    private final String name;

    @Schema(description = "주소", example = "서울 마포구 ...", nullable = true)
    private final String address;

    @Schema(description = "위도", example = "37.5573", nullable = true)
    private final Double lat;

    @Schema(description = "경도", example = "126.9259", nullable = true)
    private final Double lng;

    @Schema(description = "메모", example = "아침 9시 집합")
    private final String memo;

    @Schema(description = "생성 시각")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시각")
    private final LocalDateTime updatedAt;
}
