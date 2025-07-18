package com.example.ieumapi.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "그룹 초대 생성 요청 DTO")
public class CreateGroupInviteRequest {
    @PositiveOrZero(message = "toUserId는 0 이상의 값이어야 합니다.")
    @Schema(description = "초대할 사용자 ID (선택). null이면 링크 초대", example = "456")
    private Long toUserId;

    @Schema(description = "초대 만료까지 지속 시간(일 단위)", example = "7")
    private Integer validDays = 7;
}
