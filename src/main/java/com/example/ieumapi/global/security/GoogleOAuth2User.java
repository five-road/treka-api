package com.example.ieumapi.global.security;

import com.example.ieumapi.user.domain.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GoogleOAuth2User implements OAuth2User {
    private final String id;
    private final String email;
    private final String name;
    private final UserRole role;
    private final String picture;
    private final Map<String, Object> attributes;

    public GoogleOAuth2User(String id, String email, String name, UserRole role, String picture, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.picture = picture;
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
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }
}

