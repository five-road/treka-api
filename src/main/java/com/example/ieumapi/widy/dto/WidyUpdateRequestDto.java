package com.example.ieumapi.widy.dto;

import com.example.ieumapi.widy.domain.WidyEmotion;
import com.example.ieumapi.widy.domain.WidyScope;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Set;
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
    @Schema(description = "위디 제목", example = "수정된 맛집 탐방")
    private String title;

    @Schema(description = "위디 내용", example = "사실은 맛집이 아니었다.")
    private String content;

    @Schema(description = "일정 ID", example = "11")
    private Long scheduleId;

    @Schema(description = "그룹 ID (scope가 GROUP일 경우 필수)", example = "6")
    private Long groupId;

    @Schema(description = "날짜", example = "2024-07-25")
    private LocalDate date;

    @Schema(description = "사진 ID", example = "2")
    private Long photoId;

    @Schema(description = "감정 목록", example = "[힐링, 즉흥]")
    private Set<WidyEmotion> emotions;

    @Schema(description = "공개 범위", example = "GROUP")
    private WidyScope scope;
}
