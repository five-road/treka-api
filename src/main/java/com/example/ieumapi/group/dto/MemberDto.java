package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "그룹 멤버 정보 DTO")
public class MemberDto {
    @Schema(description = "사용자 ID", example = "456")
    private final Long userId;

    @Schema(description = "닉네임", example = "hana_lee")
    private final String nickname;

    @Schema(description = "실명", example = "이하나")
    private final String name;

    @Schema(description = "이메일", example = "hana@example.com")
    private final String email;

    @Schema(description = "역할", example = "OWNER")
    private final String role;
}

