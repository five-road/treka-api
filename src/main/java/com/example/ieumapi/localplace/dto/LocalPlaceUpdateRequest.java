package com.example.ieumapi.localplace.dto;

import com.example.ieumapi.localplace.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LocalPlaceUpdateRequest {
    @Schema(description = "장소 이름", example = "서울역")
    private String name;
    @Schema(description = "장소 설명", example = "대한민국의 수도 서울의 관문")
    private String description;
    @Schema(description = "주소", example = "서울특별시 용산구 한강대로 405")
    private String address;
    @Schema(description = "장소 카테고리", example = "ETC", implementation = PlaceCategory.class,
            allowableValues = {"ATTRACTION", "CULTURAL_FACILITY", "FESTIVAL", "TOUR_COURSE", "LEISURE_SPORTS", "ACCOMMODATION", "SHOPPING", "RESTAURANT", "ETC"})
    private PlaceCategory category;
}
