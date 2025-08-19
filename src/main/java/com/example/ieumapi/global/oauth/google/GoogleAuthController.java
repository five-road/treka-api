package com.example.ieumapi.global.oauth.google;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> googleAuth(@RequestHeader("Authorization") String accessToken) {
        Map<String, String> tokens = googleAuthService.googleLoginOrRegister(accessToken);
        return ResponseEntity.ok(tokens);
    }
}
