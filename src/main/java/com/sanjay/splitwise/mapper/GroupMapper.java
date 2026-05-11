package com.sanjay.splitwise.mapper;

import com.sanjay.splitwise.dto.GroupResponseDTO;
import com.sanjay.splitwise.entity.Group;

public class GroupMapper {
    public static GroupResponseDTO toDTO(Group group){
        return GroupResponseDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
