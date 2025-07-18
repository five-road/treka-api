package com.example.ieumapi.user.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.user.dto.*;
import com.example.ieumapi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    public CommonResponse<Void> signup(@Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
        return CommonResponse.success();
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

    @SuccessMessage("사용자 검색에 성공했습니다.")
    @GetMapping
    public CursorPageResponse<UserSearchResultDto> searchUsers(
            @RequestParam @NotBlank(message = "query 파라미터는 필수입니다.") String query,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size는 1 이상의 정수여야 합니다.") @Max(value = 100, message = "size는 100 이하로 설정해주세요.") int size,
            @RequestParam(required = false) String cursor
    ) {
        CursorPageResponse<UserSearchResultDto> response = userService.searchUsersCursor(query, size, cursor);
        return response;
    }
}
