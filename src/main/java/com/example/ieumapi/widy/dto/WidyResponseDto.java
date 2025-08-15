package com.example.ieumapi.widy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidyResponseDto {
    private Long id;
    private Long userId;
    private String content;
    private Long photoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

