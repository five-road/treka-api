package com.example.ieumapi.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Schema(description = "커서 기반 페이징 응답")
public class CursorPageResponse<T> {
    @Schema(description = "데이터 리스트")
    private final List<T> data;

    @Schema(description = "다음 조회를 위한 커서 토큰 (Base64 인코딩된 userId)", example = "MTAx")
    private final String nextCursor;

    @Schema(description = "뒤에 더 조회할 데이터가 있는지 여부", example = "true")
    private final boolean hasNext;
}
