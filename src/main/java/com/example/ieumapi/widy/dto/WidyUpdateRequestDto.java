package com.example.ieumapi.widy.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WidyUpdateRequestDto {
    private String title;
    private String content;
    private Long scheduleId;
    private Long groupId;
    private LocalDate date;
    private Long photoId;
    private java.util.Set<com.example.ieumapi.widy.domain.WidyEmotion> emotions;
}

