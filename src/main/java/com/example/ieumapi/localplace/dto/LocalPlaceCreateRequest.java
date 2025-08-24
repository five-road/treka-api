package com.example.ieumapi.localplace.dto;

import lombok.Getter;

@Getter
public class LocalPlaceCreateRequest {
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
}
