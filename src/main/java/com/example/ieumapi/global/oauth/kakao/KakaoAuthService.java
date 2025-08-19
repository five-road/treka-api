package com.example.ieumapi.global.oauth.kakao;

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
public class KakaoAuthService {
    private final KakaoApiFeignClient kakaoApiFeignClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public Map<String, String> kakaoLoginOrRegister(String accessToken) {
        // 1. 유저 정보 조회
        JsonNode userInfo = kakaoApiFeignClient.getUserInfo("Bearer " + accessToken);
        String email = userInfo.path("kakao_account").path("email").asText();
        String nickname = userInfo.path("kakao_account").path("profile").path("nickname").asText();
        String profileImage = userInfo.path("kakao_account").path("profile").path("profile_image_url").asText("");

        // 2. 회원 조회/가입
        User user = userRepository.findByEmail(email).orElseGet(() ->
            userRepository.save(User.builder()
                .email(email)
                .name(nickname)
                .nickName(nickname)
                .password("")
                .role(UserRole.ROLE_USER)
                .imageUrl(profileImage)
                .snsType("KAKAO")
                .build())
        );

        // 3. JWT 발급
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }
}
