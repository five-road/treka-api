package com.example.ieumapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class UserSignupRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입��다.")
    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "이름", example = "홍길동")
    private String name;
}
