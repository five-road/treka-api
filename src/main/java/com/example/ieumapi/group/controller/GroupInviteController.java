package com.example.ieumapi.group.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.group.dto.*;
import com.example.ieumapi.group.service.GroupInviteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/invites")
@RequiredArgsConstructor
public class GroupInviteController {
    private final GroupInviteService inviteService;

    @Operation(summary = "그룹 초대 생성", description = "오너만 그룹 초대를 생성할 수 있습니다.")
    @SuccessMessage("그룹 초대를 생성했습니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupInviteResponse createInvite(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateGroupInviteRequest request
    ) {
        return inviteService.createInvite(groupId, request);
    }

    @Operation(summary = "그룹 링크 초대 생성", description = "오너만 그룹 링크 초대를 생성할 수 있습니다.")
    @SuccessMessage("그룹 링크 초대를 생성했습니다.")
    @PostMapping("/link")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupInviteResponse createLinkInvite(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateLinkInviteRequest request
    ) {
        return inviteService.createLinkInvite(groupId, request);
    }

    @Operation(summary = "그룹 초대 목록 조회", description = "그룹 멤버가 생성한 모든 초대를 조회합니다.")
    @SuccessMessage("그룹 초대 목록을 불러왔습니다.")
    @GetMapping
    public List<InviteDto> listInvites(
            @PathVariable Long groupId
    ) {
        return inviteService.getGroupInvites(groupId);
    }

    @Operation(summary = "초대 상세 조회", description = "초대 코드를 통해 초대 상세 정보를 가져옵니다.")
    @SuccessMessage("초대 상세 정보를 불러왔습니다.")
    @GetMapping("/{code}")
    public InviteDetailDto getInviteDetail(
            @PathVariable String code
    ) {
        return inviteService.getInviteDetail(code);
    }

    @Operation(summary = "초대 수락", description = "초대 코드를 통해 그룹에 참여 요청을 수락합니다.")
    @SuccessMessage("그룹 참여를 완료했습니다.")
    @PostMapping("/{code}/accept")
    public AcceptInviteResponse acceptInvite(
            @PathVariable String code
    ) {
        return inviteService.acceptInvite(code);
    }

    @Operation(summary = "초대 거절", description = "초대를 거절합니다.")
    @SuccessMessage("초대를 거절했습니다.")
    @PostMapping("/{code}/decline")
    public DeclineInviteResponse declineInvite(
            @PathVariable String code
    ) {
        return inviteService.declineInvite(code);
    }

    @Operation(
            summary = "그룹 초대 삭제",
            description = "그룹의 초대를 삭제합니다. 그룹 오너만 가능합니다."
    )
    @SuccessMessage("초대를 삭제했습니다.")
    @DeleteMapping("/{inviteId}")
    public CommonResponse<Void> deleteInvite(
            @PathVariable Long groupId,
            @PathVariable Long inviteId
    ) {
        inviteService.deleteInvite(groupId, inviteId);
        return CommonResponse.success();
    }
}
