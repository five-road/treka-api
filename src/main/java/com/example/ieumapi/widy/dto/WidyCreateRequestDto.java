package com.example.ieumapi.widy.dto;

import com.example.ieumapi.widy.domain.Widy;
import com.example.ieumapi.widy.domain.WidyEmotion;
import com.example.ieumapi.widy.domain.WidyImage;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter
public class WidyCreateRequestDto {
    @NotNull
    private Long userId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private Long scheduleId;
    private Long groupId;
    private LocalDate date;
    private Set<WidyEmotion> emotions;

    public Widy toEntity() {
        return Widy.builder()
            .userId(userId)
            .title(title)
            .content(content)
            .scheduleId(scheduleId)
            .groupId(groupId)
            .date(date)
            .widyEmotionList(emotions)
            .build();
    }
}

