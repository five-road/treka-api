package com.example.ieumapi.global.security;

import com.example.ieumapi.user.domain.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KakaoOAuth2User implements OAuth2User {
    private final String id;
    private final String email;
    private final String nickname;
    private final UserRole role;
    private final String profileImageUrl;
    private final Map<String, Object> attributes;

    public KakaoOAuth2User(String id, String email, String nickname, UserRole role, String profileImageUrl, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getName() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}

