package com.example.ieumapi.user.controller;

import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.user.dto.UserSignupRequest;
import com.example.ieumapi.user.dto.UserSignupResponse;
import com.example.ieumapi.user.dto.UserLoginRequest;
import com.example.ieumapi.user.dto.UserLoginResponse;
import com.example.ieumapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    @SuccessMessage("회원가입이 성공적으로 완료되었습니다.")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public UserLoginResponse login(@Valid @RequestBody UserLoginRequest request, HttpServletResponse response) {
        UserLoginResponse loginResponse = userService.login(request);
        if (loginResponse.getAccessToken() != null) {
            response.setHeader("Authorization", "Bearer " + loginResponse.getAccessToken());
        }
        return loginResponse;
    }
}
