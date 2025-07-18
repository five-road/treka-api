package com.example.ieumapi.user.service;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.dto.UserSearchResultDto;
import com.example.ieumapi.user.dto.UserSignupRequest;
import com.example.ieumapi.user.dto.UserLoginRequest;
import com.example.ieumapi.user.dto.UserLoginResponse;
import com.example.ieumapi.user.exception.UserException;
import com.example.ieumapi.user.repository.UserRepository;
import com.example.ieumapi.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.ieumapi.user.exception.UserErrorCode.INVALID_CREDENTIALS;
import static com.example.ieumapi.user.exception.UserErrorCode.USER_DUPLICATED;
import static com.example.ieumapi.user.domain.UserRole.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserException(USER_DUPLICATED);
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickName(request.getNickName())
                .role(ROLE_USER)
                .isActive(true)
                .build();
        userRepository.save(user);
    }

    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(INVALID_CREDENTIALS);
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return UserLoginResponse.builder().accessToken(token).build();
    }

    public CursorPageResponse<UserSearchResultDto> searchUsersCursor(
            String query,
            int size,
            String cursor
    ) {
        long cursorId = decodeCursor(cursor);
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.ASC, "userId"));
        List<User> users = userRepository
                .findByNickNameContainingIgnoreCaseOrNameContainingIgnoreCaseAndUserIdGreaterThanOrderByUserIdAsc(
                        query, query, cursorId, pageable
                );

        boolean hasNext = users.size() > size;
        if (hasNext) {
            users = users.subList(0, size);
        }

        List<UserSearchResultDto> data = users.stream()
                .map(u -> new UserSearchResultDto(
                        u.getUserId(),
                        u.getNickName(),
                        u.getName(),
                        u.getImageUrl(),
                        u.getSnsType(),
                        u.isGuest()
                ))
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            Long lastId = users.get(users.size() - 1).getUserId();
            nextCursor = Base64.getEncoder()
                    .encodeToString(String.valueOf(lastId).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    private long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return 0L;
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cursor 형식이 잘못되었습니다.");
        }
    }
}
