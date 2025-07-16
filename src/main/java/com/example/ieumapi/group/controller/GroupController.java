package com.example.ieumapi.group.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.group.dto.*;
import com.example.ieumapi.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@Validated
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @Operation(summary = "그룹 생성", description = "새로운 그룹을 생성합니다.")
    @SuccessMessage("그룹을 생성했습니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(
            @Valid @RequestBody CreateGroupRequest request
    ) {
        return groupService.createGroup(request);
    }

    @Operation(summary = "커서 기반 내 그룹 조회", description = "내가 속한 그룹을 최신순으로 조회합니다.")
    @SuccessMessage("그룹 목록을 불러왔습니다.")
    @GetMapping
    public CursorPageResponse<GroupDto> listMyGroups(
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false)
            String cursor
    ) {
        return groupService.getMyGroupsCursor(size, cursor);
    }

    @Operation(summary = "그룹 상세 조회", description = "그룹의 기본 정보, 멤버, 초대 리스트를 조회합니다.")
    @SuccessMessage("그룹 상세 정보를 불러왔습니다.")
    @GetMapping("/{groupId}")
    public GroupDetailResponse getGroupDetail(
            @PathVariable Long groupId
    ) {
        return groupService.getGroupDetail(groupId);
    }

    @Operation(summary = "그룹 정보 수정", description = "오너만 그룹 이름과 타입을 수정할 수 있습니다.")
    @SuccessMessage("그룹 정보를 수정했습니다.")
    @PutMapping("/{groupId}")
    public GroupResponse updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody UpdateGroupRequest request
    ) {
        return groupService.updateGroup(groupId, request);
    }

    @Operation(summary = "그룹 삭제", description = "오너만 그룹을 삭제할 수 있습니다.")
    @SuccessMessage("그룹을 삭제했습니다.")
    @DeleteMapping("/{groupId}")
    public CommonResponse<Void> deleteGroup(
            @PathVariable Long groupId
    ) {
        groupService.deleteGroup(groupId);
        return CommonResponse.success();
    }

    @Operation(summary = "그룹 멤버 목록 조회", description = "그룹에 속한 모든 멤버를 조회합니다.")
    @SuccessMessage("그룹 멤버 목록을 불러왔습니다.")
    @GetMapping("/{groupId}/members")
    public List<MemberDto> getGroupMembers(
            @PathVariable Long groupId
    ) {
        return groupService.getGroupMembers(groupId);
    }

    @Operation(summary = "그룹 멤버 역할 변경", description = "오너만 그룹 내 특정 멤버의 역할을 변경할 수 있습니다.")
    @SuccessMessage("멤버 역할을 수정했습니다.")
    @PutMapping("/{groupId}/members/{userId}/role")
    public MemberDto updateMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateMemberRoleRequest request
    ) {
        return groupService.updateMemberRole(groupId, userId, request);
    }

    @Operation(summary = "그룹 멤버 삭제", description = "오너만 그룹 멤버를 삭제할 수 있습니다.")
    @SuccessMessage("그룹 멤버를 제거했습니다.")
    @DeleteMapping("/{groupId}/members/{userId}")
    public CommonResponse<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        groupService.removeGroupMember(groupId, userId);
        return CommonResponse.success();
    }

    @Operation(summary = "그룹 탈퇴", description = "내가 그룹에서 탈퇴합니다. 오너는 탈퇴할 수 없습니다.")
    @SuccessMessage("그룹을 탈퇴했습니다.")
    @PostMapping("/{groupId}/leave")
    public CommonResponse<Void> leaveGroup(
            @PathVariable Long groupId
    ) {
        groupService.leaveGroup(groupId);
        return CommonResponse.success();
    }
}
