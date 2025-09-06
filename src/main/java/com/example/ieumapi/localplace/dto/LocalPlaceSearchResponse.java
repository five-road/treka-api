package com.example.ieumapi.localplace.dto;

import com.example.ieumapi.localplace.domain.PlaceCategory;
import com.example.ieumapi.localplace.domain.Source;


public record LocalPlaceSearchResponse (
    String placeId,
    String name,
    String address,
    Double latitude,
    Double longitude,
    Long userId,
    String userNickName,
    Source source, // 데이터 출처 (USER, KTO)
    String ktoContentId, // 한국관광공사 contentId, nullable
    Long contentTypeId,
    PlaceCategory category,
    String imageUrl,
    String sumNailUrl
){}
