package com.example.ieumapi.friend.service;

import com.example.ieumapi.friend.domain.Friend;
import com.example.ieumapi.friend.dto.FriendDto;
import com.example.ieumapi.friend.exception.FriendError;
import com.example.ieumapi.friend.exception.FriendException;
import com.example.ieumapi.friend.repository.FriendRepository;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public CursorPageResponse<FriendDto> getFriendsCursor(int size, String cursor) {
        long cursorMillis = decodeCursor(cursor);
        long clampMillis = Math.min(cursorMillis, System.currentTimeMillis());
        LocalDateTime cursorTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(clampMillis),
                ZoneOffset.systemDefault()
        );

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 사용자가 아닙니다."));

        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("friendId")    // 동률 해결용 추가 키
        ));
        List<Friend> list = friendRepository
                .findByUserAndCreatedAtLessThan(me, cursorTime, pageable);

        boolean hasNext = list.size() > size;
        if (hasNext) list = list.subList(0, size);

        List<FriendDto> data = list.stream()
                .map(f -> {
                    var u = f.getFriend();
                    return new FriendDto(
                            u.getUserId(),
                            u.getNickName(),
                            u.getName(),
                            u.getImageUrl(),
                            u.getSnsType(),
                            u.isGuest(),
                            f.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            long lastMillis = list.get(list.size() - 1)
                    .getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            nextCursor = Base64.getEncoder()
                    .encodeToString(String.valueOf(lastMillis).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    private long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return System.currentTimeMillis();
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursor 형식이 잘못되었습니다.");
        }
    }

    @Transactional
    public void deleteFriend(Long friendUserId) {
        if (friendUserId == null || friendUserId <= 0) {
            throw new FriendException(FriendError.INVALID_USER_ID);
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.equals(friendUserId)) {
            throw new FriendException(FriendError.SELF_UNFRIEND_NOT_ALLOWED);
        }
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new FriendException(FriendError.INVALID_USER_ID));
        User friendUser = userRepository.findById(friendUserId)
                .orElseThrow(() -> new FriendException(FriendError.FRIEND_NOT_FOUND));

        boolean exists = friendRepository.existsByUserAndFriend(currentUser, friendUser);
        if (!exists) {
            throw new FriendException(FriendError.FRIEND_NOT_FOUND);
        }

        friendRepository.deleteByUserIdAndFriendId(currentUserId, friendUserId);
        friendRepository.deleteByUserIdAndFriendId(friendUserId, currentUserId);
    }
}
