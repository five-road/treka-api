package com.example.ieumapi.localplace.dto;

import com.example.ieumapi.localplace.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LocalPlaceCreateRequest {
    @Schema(description = "장소 이름", example = "서울역")
    @NotBlank(message = "장소 이름은 필수입니다.")
    private String name;

    @Schema(description = "장소 설명", example = "대한민국의 수도 서울의 관문")
    private String description;

    @Schema(description = "주소", example = "서울특별시 용산구 한강대로 405")
    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @Schema(description = "위도", example = "37.5546788")
    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @Schema(description = "경도", example = "126.970609")
    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @Schema(description = "장소 카테고리", example = "ETC", implementation = PlaceCategory.class,
            allowableValues = {"ATTRACTION", "CULTURAL_FACILITY", "FESTIVAL", "TOUR_COURSE", "LEISURE_SPORTS", "ACCOMMODATION", "SHOPPING", "RESTAURANT", "ETC"})
    @NotNull(message = "카테고리는 필수입니다.")
    private PlaceCategory category;

    @Schema(description = "영업 시간", example = "08:00 ~ 20:00")
    private String businessHours;

    @Schema(description = "주차 가능 여부", example = "가능/불가능")
    private String parking;
}
