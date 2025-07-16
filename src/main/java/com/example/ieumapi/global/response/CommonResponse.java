package com.example.ieumapi.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "성공 응답의 공통 래퍼")
public class CommonResponse<T> {
    @Schema(description = "응답 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "추가 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static CommonResponse<Void> success() {
        return success(null);
    }
}