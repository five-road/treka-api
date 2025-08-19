package com.example.ieumapi.global.oauth.google;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "google-api", url = "https://www.googleapis.com")
public interface GoogleApiFeignClient {

    @GetMapping("/oauth2/v2/userinfo")
    JsonNode getUserInfo(@RequestHeader("Authorization") String accessToken);
}
