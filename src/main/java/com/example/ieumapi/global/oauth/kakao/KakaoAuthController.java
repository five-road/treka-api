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

    @GetMapping
    public ResponseEntity<Map<String, String>> kakaoAuth(@RequestParam String code) {
        Map<String, String> tokens = kakaoAuthService.kakaoLoginOrRegister(code);
        return ResponseEntity.ok(tokens);
    }
}

