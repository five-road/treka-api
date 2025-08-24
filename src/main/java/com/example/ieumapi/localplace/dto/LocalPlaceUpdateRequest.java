package com.example.ieumapi.localplace.dto;

import lombok.Getter;

@Getter
public class LocalPlaceUpdateRequest {
    private String name;
    private String description;
    private String address;
}
