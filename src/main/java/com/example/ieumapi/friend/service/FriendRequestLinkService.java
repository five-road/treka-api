package com.example.ieumapi.friend.service;

import com.example.ieumapi.friend.domain.Friend;
import com.example.ieumapi.friend.domain.FriendLinkStatus;
import com.example.ieumapi.friend.domain.FriendRequestLink;
import com.example.ieumapi.friend.dto.AcceptFriendLinkResponse;
import com.example.ieumapi.friend.dto.CreateFriendLinkRequest;
import com.example.ieumapi.friend.dto.DeclineFriendLinkResponse;
import com.example.ieumapi.friend.dto.FriendLinkInfoResponse;
import com.example.ieumapi.friend.exception.FriendRequestError;
import com.example.ieumapi.friend.exception.FriendRequestException;
import com.example.ieumapi.friend.exception.FriendRequestLinkError;
import com.example.ieumapi.friend.exception.FriendRequestLinkException;
import com.example.ieumapi.friend.repository.FriendRepository;
import com.example.ieumapi.friend.repository.FriendRequestLinkRepository;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class FriendRequestLinkService {

    private final FriendRequestLinkRepository linkRepo;
    private final UserRepository userRepo;
    private final FriendRepository friendRepository;

    public FriendRequestLinkService(FriendRequestLinkRepository linkRepo, UserRepository userRepo, FriendRepository friendRepository) {
        this.linkRepo = linkRepo;
        this.userRepo = userRepo;
        this.friendRepository = friendRepository;
    }

    public FriendLinkInfoResponse createLink(CreateFriendLinkRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new FriendRequestLinkException(FriendRequestLinkError.UNAUTHORIZED);
        }
        User fromUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new FriendRequestException(FriendRequestError.USER_NOT_FOUND));

        LocalDateTime expiresAt = request.expiresAt();
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new FriendRequestLinkException(FriendRequestLinkError.INVALID_EXPIRES_AT);
        }

        FriendRequestLink link = FriendRequestLink.builder()
                .inviteCode(UUID.randomUUID().toString())
                .fromUser(fromUser)
                .expiresAt(expiresAt)
                .build();
        linkRepo.save(link);

        return new FriendLinkInfoResponse(
                link.getInviteCode(),
                link.getStatus(),
                link.getCreatedAt(),
                link.getExpiresAt(),
                link.getFromUser().getUserId(),
                link.getToUser()!=null ? link.getToUser().getUserId() : null,
                link.getToUser()!=null ? link.getToUser().getName():null,
                link.getToUser()!=null?link.getToUser().getNickName():null,
                link.getToUser()!=null?link.getToUser().getImageUrl():null
        );
    }

    /** 초대 링크 정보 조회 */
    public FriendLinkInfoResponse getLinkInfo(String inviteCode) {
        FriendRequestLink link = linkRepo.findByInviteCode(inviteCode)
                .orElseThrow(() -> new FriendRequestLinkException(FriendRequestLinkError.INVITE_NOT_FOUND));
        if (link.getExpiresAt()!=null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new FriendRequestLinkException(FriendRequestLinkError.INVITE_EXPIRED);
        }
        return new FriendLinkInfoResponse(
                link.getInviteCode(),
                link.getStatus(),
                link.getCreatedAt(),
                link.getExpiresAt(),
                link.getFromUser().getUserId(),
                link.getToUser()!=null ? link.getToUser().getUserId() : null,
                link.getToUser()!=null ? link.getToUser().getName():null,
                link.getToUser()!=null ? link.getToUser().getNickName():null,
                link.getToUser()!=null?link.getToUser().getImageUrl():null
        );
    }

    public AcceptFriendLinkResponse acceptLink(String inviteCode) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new FriendRequestLinkException(FriendRequestLinkError.UNAUTHORIZED);
        }
        FriendRequestLink link = linkRepo.findByInviteCode(inviteCode)
                .orElseThrow(() -> new FriendRequestLinkException(FriendRequestLinkError.INVITE_NOT_FOUND));
        if (link.getStatus() != FriendLinkStatus.PENDING) {
            throw new FriendRequestLinkException(FriendRequestLinkError.INVITE_ALREADY_PROCESSED);
        }
        if (link.getExpiresAt()!=null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new FriendRequestLinkException(FriendRequestLinkError.INVITE_EXPIRED);
        }
        User toUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new FriendRequestLinkException(FriendRequestLinkError.USER_NOT_FOUND));

        link.setStatus(FriendLinkStatus.ACCEPTED);
        link.setToUser(toUser);

        User from = link.getFromUser();
        Friend fromTo = Friend.builder().user(from).friend(toUser).build();
        Friend toFrom = Friend.builder().user(toUser).friend(from).build();
        friendRepository.save(fromTo);
        friendRepository.save(toFrom);

        FriendRequestLink updated=link.toBuilder()
                .status(FriendLinkStatus.ACCEPTED)
                .toUser(toUser)
                .build();

        return new AcceptFriendLinkResponse(
                updated.getInviteCode(),
                updated.getStatus(),
                updated.getFromUser().getUserId(),
                toUser.getUserId(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }

    /** 초대 링크 거절 */
    public DeclineFriendLinkResponse declineLink(String inviteCode) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new FriendRequestLinkException(FriendRequestLinkError.UNAUTHORIZED);
        }
        FriendRequestLink link = linkRepo.findByInviteCode(inviteCode)
                .orElseThrow(() -> new FriendRequestLinkException(FriendRequestLinkError.INVITE_NOT_FOUND));
        if (link.getStatus() != FriendLinkStatus.PENDING) {
            throw new FriendRequestLinkException(FriendRequestLinkError.INVITE_ALREADY_PROCESSED);
        }
        if (link.getExpiresAt()!=null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new FriendRequestLinkException(FriendRequestLinkError.INVITE_EXPIRED);
        }
        User toUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new FriendRequestLinkException(FriendRequestLinkError.USER_NOT_FOUND));

        link.setStatus(FriendLinkStatus.DECLINED);
        link.setToUser(toUser);

        FriendRequestLink updated = link.toBuilder()
                .status(FriendLinkStatus.DECLINED)
                .toUser(toUser)
                .build();

        return new DeclineFriendLinkResponse(
                updated.getInviteCode(),
                updated.getStatus(),
                updated.getFromUser().getUserId(),
                toUser.getUserId(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }
}