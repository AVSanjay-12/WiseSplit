package com.sanjay.splitwise.mapper;

import com.sanjay.splitwise.dto.GroupIndividualMemberResponseDTO;
import com.sanjay.splitwise.dto.GroupMemberResponseDTO;
import com.sanjay.splitwise.entity.GroupMember;

public class GroupIndividualMemberMapper {

    public static GroupIndividualMemberResponseDTO toDTO(GroupMember groupMember) {

        return GroupIndividualMemberResponseDTO.builder()

                .userId(groupMember.getUser().getId())
                .userName(groupMember.getUser().getName())
                .email(groupMember.getUser().getEmail())
                .joinedAt(groupMember.getJoinedAt())
                .build();
    }
}