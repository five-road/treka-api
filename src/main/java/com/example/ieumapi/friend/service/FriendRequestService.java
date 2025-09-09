package com.example.ieumapi.friend.service;

import com.example.ieumapi.friend.domain.Friend;
import com.example.ieumapi.friend.domain.FriendRequest;
import com.example.ieumapi.friend.domain.RequestStatus;
import com.example.ieumapi.friend.dto.*;
import com.example.ieumapi.friend.exception.FriendRequestError;
import com.example.ieumapi.friend.exception.FriendRequestException;
import com.example.ieumapi.friend.repository.FriendRepository;
import com.example.ieumapi.friend.repository.FriendRequestRepository;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.util.SecurityUtils;
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendRequestService {
    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;
    private final FriendRepository friendRepository;

    public FriendRequestResponse sendFriendRequest(CreateFriendRequestRequest req) {
        Long toUserId = req.getToUserId();
        if (toUserId == null || toUserId <= 0) {
            throw new FriendRequestException(FriendRequestError.INVALID_TO_USER_ID);
        }
        Long fromUserId = SecurityUtils.getCurrentUserId();
        if (toUserId.equals(fromUserId)) {
            throw new FriendRequestException(FriendRequestError.SELF_REQUEST_NOT_ALLOWED);
        }
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new FriendRequestException(FriendRequestError.USER_NOT_FOUND));
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new FriendRequestException(FriendRequestError.INVALID_TO_USER_ID));

        if (friendRepository.existsByUserAndFriend(fromUser, toUser)) {
            throw new FriendRequestException(FriendRequestError.ALREADY_FRIEND);
        }
        if (requestRepository.existsByFromUserAndToUserAndStatus(fromUser, toUser, RequestStatus.PENDING)) {
            throw new FriendRequestException(FriendRequestError.REQUEST_ALREADY_EXISTS);
        }

        FriendRequest entity = FriendRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        requestRepository.save(entity);

        return new FriendRequestResponse(
                entity.getRequestId(),
                fromUserId,
                toUserId,
                entity.getStatus().name(),
                entity.getCreatedAt()
        );
    }

    public CursorPageResponse<FriendRequestDto> getIncomingRequestsCursor(int size, String cursor) {
        long cursorId = decodeCursor(cursor);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 사용자가 아닙니다."));

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "requestId"));
        List<FriendRequest> list = requestRepository
                .findByToUserAndStatusAndRequestIdLessThanOrderByRequestIdDesc(
                        currentUser, RequestStatus.PENDING, cursorId, pageable
                );

        boolean hasNext = list.size() > size;
        if (hasNext) {
            list = list.subList(0, size);
        }

        List<FriendRequestDto> data = list.stream()
                .map(req -> new FriendRequestDto(
                        req.getRequestId(),
                        req.getFromUser().getUserId(),
                        req.getToUser().getUserId(),
                        req.getStatus().name(),
                        req.getCreatedAt(),
                        req.getUpdatedAt(),
                        req.getFromUser().getName(),
                        req.getFromUser().getNickName(),
                        req.getFromUser().getImageUrl(),
                        req.getFromUser().getEmail(),
                        req.getToUser().getName(),
                        req.getToUser().getNickName(),
                        req.getToUser().getImageUrl(),
                        req.getToUser().getEmail()
                ))
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            Long lastId = list.get(list.size() - 1).getRequestId();
            nextCursor = Base64.getEncoder()
                    .encodeToString(String.valueOf(lastId).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    public CursorPageResponse<FriendRequestDto> getOutgoingRequestsCursor(int size, String cursor) {
        long cursorId = decodeCursor(cursor);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 사용자가 아닙니다."));

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "requestId"));
        List<FriendRequest> list = requestRepository
                .findByFromUserAndRequestIdLessThanOrderByRequestIdDesc(
                        currentUser, cursorId, pageable
                );

        boolean hasNext = list.size() > size;
        if (hasNext) {
            list = list.subList(0, size);
        }

        List<FriendRequestDto> data = list.stream()
                .map(req -> new FriendRequestDto(
                        req.getRequestId(),
                        req.getFromUser().getUserId(),
                        req.getToUser().getUserId(),
                        req.getStatus().name(),
                        req.getCreatedAt(),
                        req.getUpdatedAt(),
                        req.getFromUser().getName(),
                        req.getFromUser().getNickName(),
                        req.getFromUser().getImageUrl(),
                        req.getFromUser().getEmail(),
                        req.getToUser().getName(),
                        req.getToUser().getNickName(),
                        req.getToUser().getImageUrl(),
                        req.getToUser().getEmail()
                ))
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            Long lastId = list.get(list.size() - 1).getRequestId();
            nextCursor = Base64.getEncoder()
                    .encodeToString(String.valueOf(lastId).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    private long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return Long.MAX_VALUE;
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursor 형식이 잘못되었습니다.");
        }
    }

    @Transactional
    public AcceptFriendRequestResponse acceptRequest(Long requestId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        FriendRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new FriendRequestException(FriendRequestError.REQUEST_NOT_FOUND));

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new FriendRequestException(FriendRequestError.REQUEST_NOT_PENDING);
        }
        if (!req.getToUser().getUserId().equals(currentUserId)) {
            throw new FriendRequestException(FriendRequestError.FORBIDDEN);
        }

        req.setStatus(RequestStatus.ACCEPTED);
        req.setUpdatedAt(LocalDateTime.now());

        User from = req.getFromUser();
        User to = req.getToUser();
        Friend fromTo = Friend.builder().user(from).friend(to).build();
        Friend toFrom = Friend.builder().user(to).friend(from).build();
        friendRepository.save(fromTo);
        friendRepository.save(toFrom);

        return new AcceptFriendRequestResponse(
                req.getRequestId(),
                from.getUserId(),
                to.getUserId(),
                req.getStatus().name(),
                req.getCreatedAt(),
                req.getUpdatedAt()
        );
    }

    @Transactional
    public DeclineFriendRequestResponse declineRequest(Long requestId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        FriendRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new FriendRequestException(FriendRequestError.REQUEST_NOT_FOUND));

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new FriendRequestException(FriendRequestError.REQUEST_NOT_PENDING);
        }
        if (!req.getToUser().getUserId().equals(currentUserId)) {
            throw new FriendRequestException(FriendRequestError.FORBIDDEN);
        }

        req.setStatus(RequestStatus.DECLINED);
        req.setUpdatedAt(LocalDateTime.now());

        return new DeclineFriendRequestResponse(
                req.getRequestId(),
                req.getFromUser().getUserId(),
                req.getToUser().getUserId(),
                req.getStatus().name(),
                req.getCreatedAt(),
                req.getUpdatedAt()
        );
    }

    @Transactional
    public void cancelRequest(Long requestId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        FriendRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new FriendRequestException(FriendRequestError.REQUEST_NOT_FOUND));

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new FriendRequestException(FriendRequestError.REQUEST_NOT_PENDING);
        }
        if (!req.getFromUser().getUserId().equals(currentUserId)) {
            throw new FriendRequestException(FriendRequestError.FORBIDDEN);
        }

        requestRepository.delete(req);
    }
}
