package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "초대 수락 응답 DTO")
public class AcceptInviteResponse {
    @Schema(description = "그룹 ID", example = "10")
    private final Long groupId;

    @Schema(description = "사용자 ID (수락한 멤버)", example = "456")
    private final Long userId;

    @Schema(description = "멤버 역할", example = "MEMBER")
    private final String role;
}

