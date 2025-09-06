package com.example.ieumapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 응답: AccessToken 반환")
public class UserLoginResponse {
    @Schema(description = "JWT AccessToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String accessToken;
    private String nickName;
    private String email;
    private Long userId;
    private String name;
}

