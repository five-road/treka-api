package com.example.ieumapi.widy.dto;

import com.example.ieumapi.widy.domain.Widy;
import com.example.ieumapi.widy.domain.WidyEmotion;
import com.example.ieumapi.widy.domain.WidyScope;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Getter
public class WidyCreateRequestDto {
    @Schema(description = "사용자 ID", example = "1")
    @NotNull
    private Long userId;

    @Schema(description = "위디 제목", example = "오늘의 맛집 탐방")
    @NotBlank
    private String title;

    @Schema(description = "위디 내용", example = "오늘은 강남역 근처 맛집에 다녀왔다. 너무 맛있었다!")
    @NotBlank
    private String content;

    @Schema(description = "일정 ID", example = "10")
    private Long scheduleId;

    @Schema(description = "그룹 ID (scope가 GROUP일 경우 필수)", example = "5")
    private Long groupId;

    @Schema(description = "날짜", example = "2024-07-24")
    private LocalDate date;

    @Schema(description = "감정 목록", example = "[맛집, 신남]")
    @NotEmpty
    private Set<WidyEmotion> emotions;

    @Schema(description = "공개 범위", example = "PUBLIC")
    private WidyScope scope;

    public Widy toEntity() {
        return Widy.builder()
            .userId(userId)
            .title(title)
            .content(content)
            .scheduleId(scheduleId)
            .groupId(groupId)
            .date(date)
            .widyEmotionList(emotions)
            .scope(scope)
            .build();
    }
}
