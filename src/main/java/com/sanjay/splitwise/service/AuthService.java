package com.sanjay.splitwise.service;

import com.sanjay.splitwise.dto.LoginRequestDTO;
import com.sanjay.splitwise.dto.LoginResponseDTO;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.exception.InvalidCredentialsException;
import com.sanjay.splitwise.repository.UserRepository;
import com.sanjay.splitwise.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {

                    log.warn(
                            "Failed login attempt for email: {}",
                            request.getEmail()
                    );

                    return new InvalidCredentialsException(
                            "Invalid email or password"
                    );
                });

        boolean passwordMatches = passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            log.warn(
                    "Failed login attempt for email: {}",
                    request.getEmail()
            );

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtUtil.generateToken(user.getEmail());

        log.info(
                "User logged in successfully: {}",
                user.getEmail()
        );

        return LoginResponseDTO.builder()
                .token(token)
                .message("Login successful")
                .build();
    }
}