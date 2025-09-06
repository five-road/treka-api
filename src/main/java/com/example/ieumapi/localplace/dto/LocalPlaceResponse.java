package com.example.ieumapi.localplace.dto;

import com.example.ieumapi.localplace.domain.LocalPlace;
import com.example.ieumapi.localplace.domain.PlaceCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LocalPlaceResponse {

    private Long placeId;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long userId;
    private String userNickname;
    private PlaceCategory category;
    private String businessHours;
    private String parking;
    private List<String> imageUrls;

    public static LocalPlaceResponse from(LocalPlace localPlace, List<String> imageUrls,
        String userNickname) {
        return LocalPlaceResponse.builder()
            .placeId(localPlace.getPlaceId())
            .name(localPlace.getName())
            .description(localPlace.getDescription())
            .parking(localPlace.getParking())
            .businessHours(localPlace.getBusinessHours())
            .address(localPlace.getAddress())
            .latitude(localPlace.getLatitude())
            .longitude(localPlace.getLongitude())
            .userId(localPlace.getUserId())
            .userNickname(userNickname)
            .category(localPlace.getCategory())
            .imageUrls(imageUrls)
            .build();
    }
}
