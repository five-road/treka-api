package com.example.ieumapi.global.oauth.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> kakaoAuth(@RequestHeader("Authorization") String accessToken) {
        Map<String, String> tokens = kakaoAuthService.kakaoLoginOrRegister(accessToken);
        return ResponseEntity.ok(tokens);
    }
}
