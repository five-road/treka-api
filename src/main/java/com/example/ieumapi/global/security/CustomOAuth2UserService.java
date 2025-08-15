package com.example.ieumapi.global.security;

import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.domain.UserRole;
import com.example.ieumapi.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String KAKAO = "kakao";
    private static final String GOOGLE = "google";
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (KAKAO.equals(registrationId)) {
            // 카카오 정보 파싱
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String id = String.valueOf(attributes.get("id"));
            String email = (String) kakaoAccount.get("email");
            String nickname = (String) profile.get("nickname");
            String profileImageUrl = (String) profile.get("profile_image_url");

            // 회원 존재 여부 확인 및 회원가입
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .name(nickname)
                        .nickName(nickname) // nickName 추가
                        .password("") // password NOT NULL 대응
                        .role(UserRole.ROLE_USER)
                        .imageUrl(profileImageUrl)
                        .snsType(KAKAO)
                        .build();
                return userRepository.save(newUser);
            });

            return new KakaoOAuth2User(id, email, nickname, user.getRole(), profileImageUrl, attributes);
        } else if (GOOGLE.equals(registrationId)) {
            // 구글 정보 파싱
            String id = (String) attributes.get("sub");
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");

            // 회원 존재 여부 확인 및 회원가입
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .nickName(name) // nickName 추가
                        .password("") // password NOT NULL 대응
                        .role(UserRole.ROLE_USER)
                        .imageUrl(picture)
                        .snsType(GOOGLE)
                        .build();
                return userRepository.save(newUser);
            });

            return new GoogleOAuth2User(id, email, name, user.getRole(), picture, attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");

        }
    }

}


