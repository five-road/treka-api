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

    @PostMapping
    public ResponseEntity<Map<String, String>> googleAuth(@RequestParam String  code) {
        Map<String, String> tokens = googleAuthService.googleLoginOrRegister(code);
        return ResponseEntity.ok(tokens);
    }
}

