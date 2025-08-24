package com.example.ieumapi.user.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.user.dto.*;
import com.example.ieumapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "일반 회원가입")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> signup(@Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
        return CommonResponse.success();
    }

    @Operation(summary = "일반 로그인")
    @PostMapping("/login")
    public CommonResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return CommonResponse.success(response);
    }

    @Operation(summary = "사용자 검색")
    @GetMapping("/search")
    public CommonResponse<?> searchUsers(
            @RequestParam @NotBlank(message = "query 파라미터는 필수입니다.") String query,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size는 1 이상의 정수여야 합니다.") @Max(value = 100, message = "size는 100 이하로 설정해주세요.") int size,
            @RequestParam(required = false) String cursor
    ) {
        return CommonResponse.success(userService.searchUsersCursor(query, size, cursor));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<Void> deleteUser() {
        userService.deleteUser();
        return CommonResponse.success();
    }

}
