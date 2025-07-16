package com.example.ieumapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "사용자 검색 결과 DTO")
public class UserSearchResultDto {
    @Schema(description = "사용자 고유 ID")
    private final Long userId;
    @Schema(description = "닉네임")
    private final String nickname;
    @Schema(description = "실명")
    private final String name;
    @Schema(description = "프로필 이미지 URL", nullable = true)
    private final String imageUrl;
    @Schema(description = "가입 SNS 타입")
    private final String snsType;
    @Schema(description = "게스트 계정 여부")
    private final boolean isGuest;
}

