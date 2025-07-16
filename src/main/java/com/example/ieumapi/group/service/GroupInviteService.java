package com.example.ieumapi.group.service;

import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.group.domain.*;
import com.example.ieumapi.group.dto.*;
import com.example.ieumapi.group.exception.GroupInviteError;
import com.example.ieumapi.group.exception.GroupInviteException;
import com.example.ieumapi.group.repository.GroupInviteRepository;
import com.example.ieumapi.group.repository.GroupMemberRepository;
import com.example.ieumapi.group.repository.GroupRepository;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupInviteService {
    private final GroupRepository groupRepository;
    private final GroupInviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public GroupInviteResponse createInvite(Long groupId, CreateGroupInviteRequest req) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupInviteException(GroupInviteError.GROUP_NOT_FOUND));

        if (!group.getOwner().getUserId().equals(currentUserId)) {
            throw new GroupInviteException(GroupInviteError.FORBIDDEN);
        }

        User toUser = null;
        if (req.getToUserId() != null) {
            toUser = userRepository.findById(req.getToUserId())
                    .orElseThrow(() -> new GroupInviteException(GroupInviteError.USER_NOT_FOUND));
        }

        String raw = UUID.randomUUID().toString();
        String code = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(req.getValidDays());

        GroupInvite invite = GroupInvite.builder()
                .group(group)
                .fromUser(SecurityUtils.getCurrentUser())
                .toUser(toUser)
                .inviteCode(code)
                .createdAt(now)
                .expiresAt(expires)
                .status(com.example.ieumapi.group.domain.InviteStatus.PENDING)
                .build();
        invite = inviteRepository.save(invite);

        return new GroupInviteResponse(
                invite.getInviteId(),
                groupId,
                invite.getInviteCode(),
                invite.getCreatedAt(),
                invite.getExpiresAt(),
                invite.getStatus().name()
        );
    }

    @Transactional
    public GroupInviteResponse createLinkInvite(Long groupId, CreateLinkInviteRequest req) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupInviteException(GroupInviteError.GROUP_NOT_FOUND));
        Long currentUserId = SecurityUtils.getCurrentUser().getUserId();
        if (!group.getOwner().getUserId().equals(currentUserId)) {
            throw new GroupInviteException(GroupInviteError.FORBIDDEN);
        }

        String raw = UUID.randomUUID().toString();
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plusDays(req.getValidDays());

        GroupInvite invite = GroupInvite.builder()
                .group(group)
                .fromUser(SecurityUtils.getCurrentUser())
                .toUser(null)
                .inviteCode(code)
                .status(InviteStatus.PENDING)
                .createdAt(now)
                .expiresAt(expires)
                .build();
        invite = inviteRepository.save(invite);

        return new GroupInviteResponse(
                invite.getInviteId(),
                groupId,
                invite.getInviteCode(),
                invite.getCreatedAt(),
                invite.getExpiresAt(),
                invite.getStatus().name()
        );
    }

    @Transactional(readOnly = true)
    public List<InviteDto> getGroupInvites(Long groupId) {
        Long currentUserId = SecurityUtils.getCurrentUser().getUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupInviteException(GroupInviteError.GROUP_NOT_FOUND));

        boolean isMember = groupMemberRepository.existsByGroupGroupIdAndUserId(groupId, currentUserId);
        if (!isMember) {
            throw new GroupInviteException(GroupInviteError.FORBIDDEN);
        }

        List<GroupInvite> invites = inviteRepository.findByGroup(group);
        return invites.stream()
                .map(inv -> new InviteDto(
                        inv.getInviteId(),
                        inv.getFromUser().getUserId(),
                        inv.getToUser() != null ? inv.getToUser().getUserId() : null,
                        inv.getInviteCode(),
                        inv.getStatus().name(),
                        inv.getCreatedAt(),
                        inv.getExpiresAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InviteDetailDto getInviteDetail(String code) {
        GroupInvite invite = inviteRepository.findByInviteCode(code)
                .orElseThrow(() -> new GroupInviteException(GroupInviteError.INVITE_NOT_FOUND));

        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GroupInviteException(GroupInviteError.INVITE_EXPIRED);
        }
        if (invite.getStatus() != com.example.ieumapi.group.domain.InviteStatus.PENDING) {
            throw new GroupInviteException(GroupInviteError.INVITE_NOT_PENDING);
        }
        Group group = invite.getGroup();
        Long currentUserId = SecurityUtils.getCurrentUser().getUserId();
        boolean isMember = groupMemberRepository.existsByGroupGroupIdAndUserId(group.getGroupId(), currentUserId);
        if (!isMember && invite.getToUser() != null && !invite.getToUser().getUserId().equals(currentUserId)) {
            throw new GroupInviteException(GroupInviteError.FORBIDDEN);
        }

        return new InviteDetailDto(
                invite.getInviteId(),
                group.getGroupId(),
                group.getName(),
                invite.getFromUser().getUserId(),
                invite.getToUser() != null ? invite.getToUser().getUserId() : null,
                invite.getInviteCode(),
                invite.getStatus().name(),
                invite.getCreatedAt(),
                invite.getExpiresAt()
        );
    }

    @Transactional
    public AcceptInviteResponse acceptInvite(String code) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(()->new GroupInviteException(GroupInviteError.USER_NOT_FOUND));

        GroupInvite invite = inviteRepository.findByInviteCode(code)
                .orElseThrow(() -> new GroupInviteException(GroupInviteError.INVITE_NOT_FOUND));

        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GroupInviteException(GroupInviteError.INVITE_EXPIRED);
        }
        if (invite.getStatus() != com.example.ieumapi.group.domain.InviteStatus.PENDING) {
            throw new GroupInviteException(GroupInviteError.INVITE_NOT_PENDING);
        }

        if (invite.getToUser() != null && !invite.getToUser().getUserId().equals(currentUserId)) {
            throw new GroupInviteException(GroupInviteError.FORBIDDEN);
        }

        Group group = invite.getGroup();
        boolean already = groupMemberRepository.existsByGroupGroupIdAndUserId(group.getGroupId(), currentUserId);
        if (already) {
            throw new GroupInviteException(GroupInviteError.INVITE_NOT_PENDING); // reuse error or define NEW
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(currentUser)
                .role(MemberRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();
        groupMemberRepository.save(member);

        invite.setStatus(InviteStatus.ACCEPTED);
        inviteRepository.save(invite);

        return new AcceptInviteResponse(
                group.getGroupId(),
                currentUserId,
                member.getRole().name()
        );
    }

    @Transactional
    public DeclineInviteResponse declineInvite(String code) {
        Long currentUserId = SecurityUtils.getCurrentUser().getUserId();
        GroupInvite invite = inviteRepository.findByInviteCode(code)
                .orElseThrow(() -> new GroupInviteException(GroupInviteError.INVITE_NOT_FOUND));

        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GroupInviteException(GroupInviteError.INVITE_EXPIRED);
        }
        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new GroupInviteException(GroupInviteError.INVITE_NOT_PENDING);
        }
        if (invite.getToUser() != null && !invite.getToUser().getUserId().equals(currentUserId)) {
            throw new GroupInviteException(GroupInviteError.FORBIDDEN);
        }
        invite.setStatus(InviteStatus.DECLINED);
        inviteRepository.save(invite);
        return new DeclineInviteResponse(
                invite.getInviteId(),
                invite.getGroup().getGroupId(),
                currentUserId,
                LocalDateTime.now()
        );
    }

    @Transactional
    public void deleteInvite(Long groupId, Long inviteId) {
        GroupInvite invite = inviteRepository
                .findByInviteIdAndGroupGroupId(inviteId, groupId)
                .orElseThrow(() -> new GroupInviteException(GroupInviteError.INVITE_NOT_FOUND));

        Long currentUserId = SecurityUtils.getCurrentUser().getUserId();
        if (!invite.getGroup().getOwner().getUserId().equals(currentUserId)) {
            throw new GroupInviteException(GroupInviteError.FORBIDDEN);
        }

        inviteRepository.deleteByInviteIdAndGroupGroupId(inviteId, groupId);
    }
}
