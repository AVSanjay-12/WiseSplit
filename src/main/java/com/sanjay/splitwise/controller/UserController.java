package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.service.UserService;
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
    public User createUser(@RequestParam String name,
                           @RequestParam String email) {

        return userService.createUser(name, email);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}