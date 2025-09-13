package com.example.ieumapi.global.oauth.kakao;

import com.example.ieumapi.global.oauth.dto.OauthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/login")
    public ResponseEntity<OauthResponse> kakaoAuth(@RequestHeader("Authorization") String accessToken) {
        OauthResponse oauthResponse = kakaoAuthService.kakaoLoginOrRegister(accessToken);
        return ResponseEntity.ok(oauthResponse);
    }

    @GetMapping("/code")
    public ResponseEntity<OauthResponse> kakaoCallback(@RequestParam("code") String code) {
        OauthResponse oauthResponse = kakaoAuthService.getKakaoToken(code);
        return ResponseEntity.ok(oauthResponse);
    }
}
