package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.LoginRequestDTO;
import com.sanjay.splitwise.dto.LoginResponseDTO;
import com.sanjay.splitwise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {

        return authService.login(request);
    }
}