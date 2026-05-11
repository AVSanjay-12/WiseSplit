package com.sanjay.splitwise.mapper;

import com.sanjay.splitwise.dto.UserResponseDTO;
import com.sanjay.splitwise.entity.User;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}