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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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

    @Transactional
    public void oauth2Signup(String accessToken) {
        // Google 사용자 정보 가져오기
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google 사용자 정보를 가져올 수 없습니다.");
        }

        Map<String, Object> userInfo = response.getBody();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        // 사용자 정보로 회원가입 처리
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException(USER_DUPLICATED);
        }

        User user = User.builder()
                .email(email)
                .name(name)
                .nickName(name) // 닉네임은 이름으로 설정
                .imageUrl(picture)
                .password("") // OAuth2 사용자는 비밀번호가 필요 없음
                .role(ROLE_USER)
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void kakaoSignup(String accessToken) {
        // Kakao 사용자 정보 가져오기
        String userInfoEndpoint = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kakao 사용자 정보를 가져올 수 없습니다.");
        }

        Map<String, Object> userInfo = response.getBody();
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) userInfo.get("properties")).get("nickname");
        String profileImage = (String) ((Map<String, Object>) userInfo.get("properties")).get("profile_image");

        // 사용자 정보로 회원가입 처리
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException(USER_DUPLICATED);
        }

        User user = User.builder()
                .email(email)
                .name(nickname)
                .nickName(nickname)
                .imageUrl(profileImage)
                .password("") // OAuth2 사용자는 비밀번호가 필요 없음
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
