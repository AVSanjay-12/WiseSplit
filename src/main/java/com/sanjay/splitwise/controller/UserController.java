package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.UserRequestDTO;
import com.sanjay.splitwise.dto.UserResponseDTO;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.mapper.UserMapper;
import com.sanjay.splitwise.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

        User user = userService.createUser(request.getName(), request.getEmail());

        return UserMapper.toDTO(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public UserResponseDTO getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);

        return UserMapper.toDTO(user);
    }

    @GetMapping
    @Operation(summary = "Get all the users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}