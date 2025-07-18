package com.example.ieumapi.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "친구 요청 생성 요청 DTO")
public class CreateFriendRequestRequest {
    @NotNull(message = "toUserId는 필수입니다.")
    @Schema(description = "친구 요청 대상 사용자 ID", example = "456", required = true)
    private Long toUserId;
}