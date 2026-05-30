package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.UserRequestDTO;
import com.sanjay.splitwise.dto.UserResponseDTO;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.mapper.UserMapper;
import com.sanjay.splitwise.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /users?name=...&email=...
    @PostMapping
    @Operation(summary = "Create a new user")
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO request) {

        User user = userService.createUser(request.getName(), request.getEmail(), request.getPassword());

        return UserMapper.toDTO(user);
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userService.getCurrentUser(email);

        return UserMapper.toDTO(user);
    }
}