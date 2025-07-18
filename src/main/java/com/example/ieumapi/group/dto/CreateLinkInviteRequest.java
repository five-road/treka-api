package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "그룹 링크 초대 생성 요청 DTO")
public class CreateLinkInviteRequest {
    @Min(value = 1, message = "validDays는 1 이상이어야 합니다.")
    @Schema(description = "초대 만료까지 지속 시간(일 단위)", example = "7")
    private Integer validDays = 7;
}
