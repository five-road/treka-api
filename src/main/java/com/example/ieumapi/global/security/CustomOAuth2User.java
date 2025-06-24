package com.example.ieumapi.global.security;

import com.example.ieumapi.user.domain.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final String id;
    private final String email;
    private final String name;
    private final UserRole role;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(String id, String email, String name, UserRole role, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
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
}

