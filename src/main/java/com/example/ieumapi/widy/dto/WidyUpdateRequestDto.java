package com.example.ieumapi.widy.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class WidyUpdateRequestDto {
    @NotBlank
    private String content;
    @NotNull
    private Long photoId;
}

