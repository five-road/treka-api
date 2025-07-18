package com.example.ieumapi.friend.controller;

import com.example.ieumapi.friend.dto.FriendDto;
import com.example.ieumapi.friend.service.FriendService;
import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friends")
@Validated
@RequiredArgsConstructor
public class FriendController {
    private final FriendService service;

    @Operation(summary = "최신 순 커서 기반 친구 목록 조회", description = "Base64 커서 무한 스크롤 조회")
    @SuccessMessage("친구 목록을 불러왔습니다.")
    @GetMapping
    public CursorPageResponse<FriendDto> listFriends(
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false)
            String cursor
    ) {
        return service.getFriendsCursor(size, cursor);
    }

    @Operation(summary = "친구 삭제", description = "지정된 사용자를 친구 목록에서 삭제합니다.")
    @SuccessMessage("친구를 삭제했습니다.")
    @DeleteMapping("/{userId}")
    public CommonResponse<Void> deleteFriend(
            @PathVariable("userId") Long userId
    ) {
        service.deleteFriend(userId);
        return CommonResponse.success();
    }
}
