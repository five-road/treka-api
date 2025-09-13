package com.example.ieumapi.global.oauth.kakao;

import com.example.ieumapi.global.jwt.JwtUtil;
import com.example.ieumapi.global.oauth.dto.OauthResponse;
import com.example.ieumapi.global.oauth.kakao.dto.KakaoTokenResponse;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.domain.UserRole;
import com.example.ieumapi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoApiFeignClient kakaoApiFeignClient;
    private final KakaoTokenFeignClient kakaoTokenFeignClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value(value = "${oauth2.kakao.client-id}")
    private String clientId;
    @Value("${oauth2.kakao.redirect-uri}")
    private String redirectUrl;
    private final static String GRANT_TYPE = "authorization_code";

    @Transactional
    public OauthResponse getKakaoToken(String code) {

        KakaoTokenResponse response = kakaoTokenFeignClient.getAccessToken(
            GRANT_TYPE,
            clientId,
            redirectUrl,
            code
        );

        String accessToken = response.getAccessToken();
        return kakaoLoginOrRegister(accessToken);
    }

    @Transactional
    public OauthResponse kakaoLoginOrRegister(String accessToken) {
        // 1. 유저 정보 조회
        JsonNode userInfo = kakaoApiFeignClient.getUserInfo("Bearer " + accessToken);
        String email = userInfo.path("kakao_account").path("email").asText();
        String nickname = userInfo.path("kakao_account").path("profile").path("nickname").asText();
        String profileImage = userInfo.path("kakao_account").path("profile")
            .path("profile_image_url").asText("");

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

        return new OauthResponse(newAccessToken, refreshToken);
    }
}
