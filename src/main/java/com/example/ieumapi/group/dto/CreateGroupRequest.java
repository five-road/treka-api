package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "그룹 생성 요청 DTO")
public class CreateGroupRequest {
    @NotBlank(message = "그룹 이름은 필수입니다.")
    @Schema(description = "그룹 이름", example = "여행 모임", required = true)
    private String name;

    @Schema(description = "그룹 설명", example = "같이 여행 계획 세우는 모임")
    private String description;
}
