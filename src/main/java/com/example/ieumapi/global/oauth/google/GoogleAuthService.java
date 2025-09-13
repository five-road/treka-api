package com.example.ieumapi.global.oauth.google;

import com.example.ieumapi.global.jwt.JwtUtil;
import com.example.ieumapi.global.oauth.dto.OauthResponse;
import com.example.ieumapi.global.oauth.google.dto.GoogleTokenResponse;
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
public class GoogleAuthService {

    private final GoogleApiFeignClient googleApiFeignClient;
    private final GoogleTokenFeignClient googleTokenFeignClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value(value = "${oauth2.google.client-id}")
    private String clientId;
    @Value(value = "${oauth2.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.google.redirect-uri}")
    private String redirectUrl;
    private final static String GRANT_TYPE = "authorization_code";

    @Transactional
    public OauthResponse getGoogleToken(String code) {
        GoogleTokenResponse response = googleTokenFeignClient.getAccessToken(
            GRANT_TYPE,
            clientId,
            clientSecret,
            redirectUrl,
            code
        );

        String accessToken = response.getAccessToken();
        return googleLoginOrRegister(accessToken);
    }

    @Transactional
    public OauthResponse googleLoginOrRegister(String accessToken) {
        // 1. 유저 정보 조회
        JsonNode userInfo = googleApiFeignClient.getUserInfo("Bearer " + accessToken);
        String email = userInfo.path("email").asText();
        String nickname = userInfo.path("name").asText();
        String profileImage = userInfo.path("picture").asText("");

        // 2. 회원 조회/가입
        User user = userRepository.findByEmail(email).orElseGet(() ->
            userRepository.save(User.builder()
                .email(email)
                .name(nickname)
                .nickName(nickname)
                .password("")
                .role(UserRole.ROLE_USER)
                .imageUrl(profileImage)
                .snsType("GOOGLE")
                .build())
        );

        // 3. JWT 발급
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new OauthResponse(newAccessToken, refreshToken);
    }
}
