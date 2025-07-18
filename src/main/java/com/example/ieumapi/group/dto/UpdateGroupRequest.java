package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "그룹 정보 수정 요청 DTO")
public class UpdateGroupRequest {
    @NotBlank(message = "그룹 이름은 null이거나 공백일 수 없습니다.")
    @Schema(description = "그룹 이름", example = "새로운 여행 모임", required = true)
    private String name;
}
