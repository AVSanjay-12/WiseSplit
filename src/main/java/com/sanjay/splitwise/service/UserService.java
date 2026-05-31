package com.sanjay.splitwise.service;

import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.exception.AlreadyExistsException;
import com.sanjay.splitwise.exception.ResourceNotFoundException;
import com.sanjay.splitwise.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String name, String email, String password) {
        if(userRepository.findByEmail(email).isPresent()) {
            log.warn("User with email {} already exist", email);
            throw new AlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        log.info("User with email {} created successfully", email);
        return userRepository.save(user);
    }

    public User getCurrentUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->{

                    log.warn("User with email {} not found", email);

                    return new ResourceNotFoundException(
                            "User not found"
                    );
                });
    }
}
