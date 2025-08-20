package com.example.ieumapi.group.service;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.group.domain.*;
import com.example.ieumapi.group.dto.*;
import com.example.ieumapi.group.exception.GroupError;
import com.example.ieumapi.group.exception.GroupException;
import com.example.ieumapi.group.repository.GroupInviteRepository;
import com.example.ieumapi.group.repository.GroupMemberRepository;
import com.example.ieumapi.group.repository.GroupRepository;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInviteRepository groupInviteRepository;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        Long ownerId = SecurityUtils.getCurrentUserId();
        if (request.getName() == null || request.getName().isBlank()) {
            throw new GroupException(GroupError.INVALID_NAME);
        }
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new GroupException(GroupError.UNAUTHORIZED));

        Group group = Group.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
        Group saved = groupRepository.save(group);

        GroupMember ownerMember = GroupMember.builder()
                .group(saved)
                .user(owner)
                .role(MemberRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();
        groupMemberRepository.save(ownerMember);

        String raw = UUID.randomUUID().toString();
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(30);

        GroupInvite invite = GroupInvite.builder()
                .group(saved)
                .fromUser(owner)
                .toUser(null) // 링크 초대
                .inviteCode(code)
                .status(InviteStatus.PENDING)
                .createdAt(now)
                .expiresAt(expires)
                .build();
        groupInviteRepository.save(invite);

        return new GroupResponse(
                saved.getGroupId(),
                saved.getName(),
                saved.getDescription(),
                ownerId,
                saved.getCreatedAt()
        );
    }

    @Transactional
    public CursorPageResponse<GroupDto> getMyGroupsCursor(int size, String cursor) {
        long cursorMillis = decodeCursor(cursor);
        LocalDateTime cursorTime = (cursorMillis <= 0)
                ? LocalDateTime.now()
                : LocalDateTime.ofInstant(Instant.ofEpochMilli(cursorMillis), ZoneOffset.UTC);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 사용자가 아닙니다."));

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "joinedAt"));
        List<GroupMember> list = groupMemberRepository
                .findByUserAndJoinedAtLessThanOrderByJoinedAtDesc(me, cursorTime, pageable);

        boolean hasNext = list.size() > size;
        if (hasNext) list = list.subList(0, size);

        LocalDateTime now = LocalDateTime.now();

        List<GroupDto> data = list.stream()
                .map(m -> {
                    Group g = m.getGroup();
                    long memberCount = groupMemberRepository.countByGroupGroupId(g.getGroupId()); // NEW
                    String inviteCode = groupInviteRepository
                            .findActiveLinkInvite(g) // NEW
                            .map(GroupInvite::getInviteCode)
                            .orElse(null);
                    return new GroupDto(
                            g.getGroupId(),
                            g.getName(),
                            g.getDescription(),
                            g.getOwner().getUserId(),
                            g.getCreatedAt(),
                            m.getJoinedAt(),
                            inviteCode,
                            memberCount
                    );
                })
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            long lastMillis = list.get(list.size() - 1)
                    .getJoinedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            nextCursor = Base64.getEncoder()
                    .encodeToString(String.valueOf(lastMillis).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    private long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return 0L;
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursor 형식이 잘못되었습니다.");
        }
    }

    @Transactional
    public GroupDetailResponse getGroupDetail(Long groupId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupError.NOT_FOUND));

        boolean isMember = groupMemberRepository.existsByGroupGroupIdAndUserId(groupId, currentUserId);
        if (!isMember) {
            throw new GroupException(GroupError.FORBIDDEN);
        }

        List<MemberDto> members = groupMemberRepository.findByGroup(group).stream()
                .map(m -> new MemberDto(
                        m.getUser().getUserId(),
                        m.getUser().getNickName(),
                        m.getUser().getName(),
                        m.getUser().getEmail(),
                        m.getRole().name()
                ))
                .collect(Collectors.toList());

        List<InviteDto> invites = groupInviteRepository.findByGroup(group).stream()
                .map(i -> new InviteDto(
                        i.getInviteId(),
                        i.getFromUser().getUserId(),
                        i.getToUser() != null ? i.getToUser().getUserId() : null,
                        i.getInviteCode(),
                        i.getStatus().name(),
                        i.getCreatedAt(),
                        i.getExpiresAt()
                ))
                .collect(Collectors.toList());

        return new GroupDetailResponse(
                group.getGroupId(),
                group.getName(),
                group.getDescription(),
                group.getOwner().getUserId(),
                group.getCreatedAt(),
                members,
                invites
        );
    }

    @Transactional
    public GroupResponse updateGroup(Long groupId, UpdateGroupRequest req) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupError.NOT_FOUND));

        if (!group.getOwner().getUserId().equals(currentUserId)) {
            throw new GroupException(GroupError.FORBIDDEN);
        }

        String newName = req.getName().trim();
        if (newName.isEmpty()) {
            throw new GroupException(GroupError.INVALID_NAME);
        }
        group.setName(newName);
        group.setDescription(req.getDescription());

        Group updated = groupRepository.save(group);

        return new GroupResponse(
                updated.getGroupId(),
                updated.getName(),
                updated.getDescription(),
                updated.getOwner().getUserId(),
                updated.getCreatedAt()
        );
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupError.NOT_FOUND));

        if (!group.getOwner().getUserId().equals(currentUserId)) {
            throw new GroupException(GroupError.FORBIDDEN);
        }

        groupRepository.delete(group);
    }

    @Transactional
    public List<MemberDto> getGroupMembers(Long groupId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupError.NOT_FOUND));

        boolean isMember = groupMemberRepository.existsByGroupGroupIdAndUserId(groupId, currentUserId);
        if (!isMember) {
            throw new GroupException(GroupError.FORBIDDEN);
        }

        List<GroupMember> members = groupMemberRepository.findByGroup(group);
        return members.stream()
                .map(m -> new MemberDto(
                        m.getUser().getUserId(),
                        m.getUser().getNickName(),
                        m.getUser().getName(),
                        m.getUser().getEmail(),
                        m.getRole().name()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberDto updateMemberRole(Long groupId, Long targetUserId, UpdateMemberRoleRequest req) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupError.NOT_FOUND));
        if (!group.getOwner().getUserId().equals(currentUserId)) {
            throw new GroupException(GroupError.FORBIDDEN);
        }
        GroupMember member = groupMemberRepository.findByGroupGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new GroupException(GroupError.MEMBER_NOT_FOUND));
        try {
            var newRole = com.example.ieumapi.group.domain.MemberRole.valueOf(req.getRole());
            member.setRole(newRole);
        } catch (IllegalArgumentException e) {
            throw new GroupException(GroupError.INVALID_ROLE);
        }
        member = groupMemberRepository.save(member);
        return new MemberDto(
                member.getUser().getUserId(),
                member.getUser().getNickName(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole().name()
        );
    }

    @Transactional
    public void removeGroupMember(Long groupId, Long targetUserId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupError.NOT_FOUND));

        if (!group.getOwner().getUserId().equals(currentUserId)) {
            throw new GroupException(GroupError.FORBIDDEN);
        }

        GroupMember member = groupMemberRepository.findByGroupGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new GroupException(GroupError.MEMBER_NOT_FOUND));

        groupMemberRepository.deleteByGroupGroupIdAndUserId(groupId, targetUserId);
    }

    @Transactional
    public void leaveGroup(Long groupId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupError.NOT_FOUND));

        if (group.getOwner().getUserId().equals(currentUserId)) {
            throw new GroupException(GroupError.FORBIDDEN);
        }

        groupMemberRepository.findByGroupGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new GroupException(GroupError.MEMBER_NOT_FOUND));

        groupMemberRepository.deleteByGroupGroupIdAndUserId(groupId, currentUserId);
    }
}
