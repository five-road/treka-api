package com.example.ieumapi.global.oauth.kakao;

import com.example.ieumapi.global.oauth.kakao.dto.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakao-token-api", url = "https://kauth.kakao.com")
public interface KakaoTokenFeignClient {
    @PostMapping("/oauth/token")
    KakaoTokenResponse getAccessToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code
    );
}
