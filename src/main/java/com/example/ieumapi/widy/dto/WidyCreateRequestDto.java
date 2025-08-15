package com.example.ieumapi.widy.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class WidyCreateRequestDto {
    @NotNull
    private Long userId;
    @NotBlank
    private String content;
    @NotNull
    private Long photoId;
}

