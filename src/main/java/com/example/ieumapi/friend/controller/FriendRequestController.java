package com.example.ieumapi.friend.controller;

import com.example.ieumapi.friend.dto.*;
import com.example.ieumapi.friend.service.FriendRequestService;
import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {
    private final FriendRequestService service;

    @Operation(summary = "친구 요청 생성", description = "다른 사용자에게 친구 요청을 보냅니다.")
    @SuccessMessage("친구 요청을 성공적으로 보냈습니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FriendRequestResponse sendFriendRequest(
            @Valid @RequestBody CreateFriendRequestRequest request
    ) {
        return service.sendFriendRequest(request);
    }

    @Operation(summary = "최신 순 커서 기반 수신된 친구 요청 조회", description = "최신 요청이 맨 위로, Base64 커서 무한 스크롤 조회")
    @SuccessMessage("친구 요청 목록을 불러왔습니다.")
    @GetMapping("/incoming")
    public CursorPageResponse<FriendRequestDto> listIncomingCursor(
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false)
            String cursor
    ) {
        return service.getIncomingRequestsCursor(size, cursor);
    }

    @Operation(summary = "최신 순 커서 기반 발신된 친구 요청 조회", description = "Base64 커서 무한 스크롤 조회")
    @SuccessMessage("발신된 친구 요청 목록을 불러왔습니다.")
    @GetMapping("/outgoing")
    public CursorPageResponse<FriendRequestDto> listOutgoingCursor(
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false)
            String cursor
    ) {
        return service.getOutgoingRequestsCursor(size, cursor);
    }

    @Operation(summary = "친구 요청 수락", description = "수신된 친구 요청을 수락합니다.")
    @SuccessMessage("친구 요청을 수락했습니다.")
    @PostMapping("/{requestId}/accept")
    public AcceptFriendRequestResponse acceptFriendRequest(
            @PathVariable("requestId") Long requestId
    ) {
        return service.acceptRequest(requestId);
    }

    @Operation(summary = "친구 요청 거절", description = "수신된 친구 요청을 거절합니다.")
    @SuccessMessage("친구 요청을 거절했습니다.")
    @PostMapping("/{requestId}/decline")
    public DeclineFriendRequestResponse declineFriendRequest(
            @PathVariable("requestId") Long requestId
    ) {
        return service.declineRequest(requestId);
    }

    @Operation(summary = "친구 요청 취소", description = "내가 보낸 친구 요청을 취소합니다.")
    @SuccessMessage("친구 요청을 취소했습니다.")
    @DeleteMapping("/{requestId}")
    public CommonResponse<Void> cancelFriendRequest(
            @PathVariable("requestId") Long requestId
    ) {
        service.cancelRequest(requestId);
        return CommonResponse.success();
    }
}
