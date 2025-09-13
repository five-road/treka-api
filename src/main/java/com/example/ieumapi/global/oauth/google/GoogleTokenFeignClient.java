package com.example.ieumapi.global.oauth.google;

import com.example.ieumapi.global.oauth.google.dto.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "google-token-api", url = "https://oauth2.googleapis.com")
public interface GoogleTokenFeignClient {
    @PostMapping("/token")
    GoogleTokenResponse getAccessToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code
    );
}
