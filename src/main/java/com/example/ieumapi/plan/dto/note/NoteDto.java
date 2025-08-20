package com.example.ieumapi.plan.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "메모 DTO")
public class NoteDto {
    @Schema(description = "메모 ID", example = "501")
    private final Long noteId;

    @Schema(description = "플랜 ID", example = "1001")
    private final Long planId;

    @Schema(description = "내용")
    private final String content;

    @Schema(description = "메모 날짜", nullable = true)
    private final LocalDate date;

    @Schema(description = "고정 여부")
    private final boolean pinned;

    @Schema(description = "생성 시각")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시각")
    private final LocalDateTime updatedAt;
}
