package com.example.ieumapi.global.oauth.google;

import com.example.ieumapi.global.oauth.dto.OauthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ResponseEntity<OauthResponse> googleAuth(@RequestHeader("Authorization") String accessToken) {
        OauthResponse oauthResponse = googleAuthService.googleLoginOrRegister(accessToken);
        return ResponseEntity.ok(oauthResponse);
    }

    @GetMapping("/code")
    public ResponseEntity<OauthResponse> googleCallback(@RequestParam("code") String code) {
        OauthResponse oauthResponse = googleAuthService.getGoogleToken(code);
        return ResponseEntity.ok(oauthResponse);
    }
}
