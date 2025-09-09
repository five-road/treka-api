package com.example.ieumapi.plan.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "메모 수정 요청(부분 수정)")
public class UpdateNoteRequest {

    @Schema(description = "메모 내용", example = "체크인 16시로 변경")
    private String content;

    @Schema(description = "메모 날짜", example = "2025-07-13")
    private LocalDate date;

    @Schema(description = "상단 고정 여부", example = "true")
    private Boolean pinned;
}
