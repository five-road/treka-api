package com.example.ieumapi.global.security;

import com.example.ieumapi.global.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof GoogleOAuth2User) {
            username = ((GoogleOAuth2User) principal).getEmail();
        } else if (principal instanceof KakaoOAuth2User) {
            username = ((KakaoOAuth2User) principal).getEmail();
        } else {
            throw new IllegalArgumentException("Unknown OAuth2User type");
        }

        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"accessToken\":\"" + accessToken + "\", \"refreshToken\":\"" + refreshToken + "\"}");
    }
}

