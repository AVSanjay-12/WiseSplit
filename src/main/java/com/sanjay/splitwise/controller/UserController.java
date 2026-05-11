package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.UserRequestDTO;
import com.sanjay.splitwise.dto.UserResponseDTO;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.mapper.UserMapper;
import com.sanjay.splitwise.service.UserService;
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
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO request) {

        User user = userService.createUser(request.getName(), request.getEmail());

        return UserMapper.toDTO(user);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);

        return UserMapper.toDTO(user);
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}