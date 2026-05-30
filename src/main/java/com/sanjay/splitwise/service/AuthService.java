package com.sanjay.splitwise.service;

import com.sanjay.splitwise.dto.LoginRequestDTO;
import com.sanjay.splitwise.dto.LoginResponseDTO;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.exception.InvalidCredentialsException;
import com.sanjay.splitwise.repository.UserRepository;
import com.sanjay.splitwise.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

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
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password")
                );

        boolean passwordMatches = passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return LoginResponseDTO.builder()
                .token(token)
                .message("Login successful")
                .build();
    }
}