package com.example.ieumapi.plan.dto.place;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "위치 수정 요청 (부분 수정 허용)")
public class UpdatePlaceRequest {
    @Schema(description = "장소 이름", example = "스타벅스 합정점")
    private String name;

    @Schema(description = "주소", example = "서울 마포구 합정동 ...")
    private String address;

    @DecimalMin(value = "-90.0", message = "위도는 -90 ~ 90 범위여야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 -90 ~ 90 범위여야 합니다.")
    @Schema(description = "위도", example = "37.5500", nullable = true)
    private Double lat;

    @DecimalMin(value = "-180.0", message = "경도는 -180 ~ 180 범위여야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 -180 ~ 180 범위여야 합니다.")
    @Schema(description = "경도", example = "126.9100", nullable = true)
    private Double lng;

    @Schema(description = "메모", example = "사람 많음")
    private String memo;
}
