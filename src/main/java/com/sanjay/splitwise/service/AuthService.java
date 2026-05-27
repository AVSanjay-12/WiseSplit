package com.sanjay.splitwise.service;

import com.sanjay.splitwise.dto.LoginRequestDTO;
import com.sanjay.splitwise.dto.LoginResponseDTO;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        boolean passwordMatches = passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return LoginResponseDTO.builder()
                .message("Login successful")
                .build();
    }
}