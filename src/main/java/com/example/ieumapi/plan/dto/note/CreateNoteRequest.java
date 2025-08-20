package com.example.ieumapi.plan.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "메모 생성 요청")
public class CreateNoteRequest {

    @NotBlank(message = "content는 필수입니다.")
    @Schema(description = "메모 내용", example = "체크인 15시, 조식 7~10시")
    private String content;

    @Schema(description = "메모 날짜(옵션)", example = "2025-07-12", nullable = true)
    private LocalDate date;

    @Schema(description = "상단 고정 여부", example = "false", defaultValue = "false")
    private boolean pinned = false;
}
