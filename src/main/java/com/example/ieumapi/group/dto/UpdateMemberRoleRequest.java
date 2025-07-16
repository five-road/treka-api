package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "그룹 멤버 역할 수정 요청 DTO")
public class UpdateMemberRoleRequest {
    @NotNull(message = "role은 필수입니다.")
    @Schema(description = "할당할 멤버 역할", example = "ADMIN", required = true)
    private String role;
}
