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

        User user = null;
        String email = null;

        if(KAKAO.equals(registrationId)){
            email = attributes.get("account_email").toString();
            user  = isKakao(attributes);
        } else if (GOOGLE.equals(registrationId)){
            email = attributes.get("email").toString();
            user = isGoogle(attributes);
        }

        // 이미 존재하는 회원일시
        if (userRepository.existsByEmail(email)) {
            return new CustomOAuth2User(Objects.requireNonNull(user), attributes);
        }

        // 새로운 유저일시
        userRepository.save(Objects.requireNonNull(user));
        return new CustomOAuth2User(user, attributes);
    }

    private User isGoogle(Map<String, Object> attributes){
        return User.builder()
            .email((String) attributes.get("email"))
            .password("")
            .nickName((String) attributes.get("nickName"))
            .name((String) attributes.get("name"))
            .role(UserRole.ROLE_USER)
            .imageUrl((String) attributes.get("picture"))
            .snsType(GOOGLE)
            .build();
    }

    private User isKakao(Map<String, Object> attributes){
        return User.builder()
            .email((String) attributes.get("account_email"))
            .password("")
            .nickName((String) attributes.get("profile_nickname"))
            .name((String) attributes.get("name"))
            .role(UserRole.ROLE_USER)
            .imageUrl((String) attributes.get("profile_image"))
            .snsType(KAKAO)
            .build();
    }
}
