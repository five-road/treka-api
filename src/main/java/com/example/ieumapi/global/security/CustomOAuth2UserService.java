package com.example.ieumapi.global.security;

import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.domain.UserRole;
import com.example.ieumapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update user info if necessary
            user = user.builder()
                    .name(name)
                    .imageUrl(picture)
                    .build();
        } else {
            user = User.builder()
                    .email(email)
                    .name(name)
                    .nickName(name) // Or generate a unique nickname
                    .imageUrl(picture)
                    .password("") // Not used for OAuth2
                    .snsType(registrationId.toUpperCase())
                    .role(UserRole.ROLE_USER)
                    .build();
        }

        userRepository.save(user);

        return new CustomOAuth2User(user, attributes);
    }
}