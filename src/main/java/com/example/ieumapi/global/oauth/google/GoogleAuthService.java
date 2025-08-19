package com.example.ieumapi.global.oauth.google;

import com.example.ieumapi.global.jwt.JwtUtil;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.domain.UserRole;
import com.example.ieumapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final GoogleApiFeignClient googleApiFeignClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public Map<String, String> googleLoginOrRegister(String accessToken) {
        // 1. 유저 정보 조회
        JsonNode userInfo = googleApiFeignClient.getUserInfo("Bearer " + accessToken);
        String email = userInfo.path("email").asText();
        String name = userInfo.path("name").asText();
        String picture = userInfo.path("picture").asText("");

        // 2. 회원 조회/가입
        User user = userRepository.findByEmail(email).orElseGet(() ->
            userRepository.save(User.builder()
                .email(email)
                .name(name)
                .nickName(name)
                .password("")
                .role(UserRole.ROLE_USER)
                .imageUrl(picture)
                .snsType("GOOGLE")
                .build())
        );

        // 3. JWT 발급
        String jwtAccessToken = jwtUtil.generateAccessToken(user.getEmail());
        String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", jwtAccessToken);
        result.put("refreshToken", jwtRefreshToken);
        return result;
    }
}
