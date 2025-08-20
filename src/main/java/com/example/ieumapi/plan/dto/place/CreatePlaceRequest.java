package com.example.ieumapi.plan.dto.place;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "위치 생성 요청")
public class CreatePlaceRequest {
    @NotBlank(message = "name은 필수입니다.")
    @Schema(description = "장소 이름", example = "스타벅스 홍대입구역점", required = true)
    private String name;

    @Schema(description = "주소", example = "서울 마포구 양화로 123")
    private String address;

    @DecimalMin(value = "-90.0", message = "위도는 -90 ~ 90 범위여야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 -90 ~ 90 범위여야 합니다.")
    @Schema(description = "위도", example = "37.5573", nullable = true)
    private Double lat;

    @DecimalMin(value = "-180.0", message = "경도는 -180 ~ 180 범위여야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 -180 ~ 180 범위여야 합니다.")
    @Schema(description = "경도", example = "126.9259", nullable = true)
    private Double lng;

    @Schema(description = "메모", example = "오전 모임 장소")
    private String memo;
}
