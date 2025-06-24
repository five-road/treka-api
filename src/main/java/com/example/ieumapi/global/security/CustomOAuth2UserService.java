package com.example.ieumapi.global.security;

import com.example.ieumapi.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (registrationId.equals("google")) {
            return new CustomOAuth2User(
                    (String) attributes.get("sub"),
                    (String) attributes.get("email"),
                    (String) attributes.get("name"),
                    UserRole.ROLE_USER,
                    attributes
            );
        } else if (registrationId.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            return new CustomOAuth2User(
                    String.valueOf(attributes.get("id")),
                    (String) kakaoAccount.get("email"),
                    (String) profile.get("nickname"),
                    UserRole.ROLE_USER,
                    attributes
            );
        }
        throw new IllegalArgumentException("Unsupported provider: " + registrationId);
    }
}

