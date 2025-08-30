package com.example.ieumapi.widy.dto;

import lombok.Getter;

@Getter
public class WidyCountResponseDto {
    private final long count;

    public WidyCountResponseDto(long count) {
        this.count = count;
    }
}
