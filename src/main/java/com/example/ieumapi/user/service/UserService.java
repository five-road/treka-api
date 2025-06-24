package com.example.ieumapi.user.service;

import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.dto.UserSignupRequest;
import com.example.ieumapi.user.dto.UserLoginRequest;
import com.example.ieumapi.user.dto.UserLoginResponse;
import com.example.ieumapi.user.exception.UserException;
import com.example.ieumapi.user.repository.UserRepository;
import com.example.ieumapi.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ieumapi.user.exception.UserErrorCode.INVALID_CREDENTIALS;
import static com.example.ieumapi.user.exception.UserErrorCode.USER_DUPLICATED;
import static com.example.ieumapi.user.domain.UserRole.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserException(USER_DUPLICATED);
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(ROLE_USER)
                .isActive(true)
                .build();
        userRepository.save(user);
    }

    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(INVALID_CREDENTIALS);
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return UserLoginResponse.builder().accessToken(token).build();
    }
}
