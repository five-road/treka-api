package com.example.ieumapi.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "친구 정보 DTO")
public class FriendDto {
    @Schema(description = "친구 사용자 ID", example = "456")
    private final Long userId;

    @Schema(description = "닉네임", example = "hana_lee")
    private final String nickname;

    @Schema(description = "실명", example = "이하나")
    private final String name;

    @Schema(description = "이메일", example = "hana@example.com")
    private final String email;

    @Schema(description = "프로필 사진 URL", nullable = true)
    private final String imageUrl;

    @Schema(description = "SNS 가입 타입")
    private final String snsType;

    @Schema(description = "게스트 여부")
    private final boolean isGuest;

    @Schema(description = "친구가 된 시각 (생성 일시)", example = "2025-06-15T10:20:30")
    private final LocalDateTime friendSince;
}
