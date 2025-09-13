package com.example.ieumapi.global.oauth.dto;


public record OauthResponse (
    String accessToken,
    String refreshToken
){}
