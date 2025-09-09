package com.example.ieumapi.global.oauth.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-api", url = "https://kapi.kakao.com")
public interface KakaoApiFeignClient {
    @GetMapping("/v2/user/me")
    JsonNode getUserInfo(@RequestHeader("Authorization") String accessToken);
}
