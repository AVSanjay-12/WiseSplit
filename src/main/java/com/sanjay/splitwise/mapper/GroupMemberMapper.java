package com.sanjay.splitwise.mapper;

import com.sanjay.splitwise.dto.GroupMemberResponseDTO;
import com.sanjay.splitwise.entity.GroupMember;

public class GroupMemberMapper {
    public static GroupMemberResponseDTO toDTO(GroupMember groupmember){
        return GroupMemberResponseDTO.builder()
                .id(groupmember.getId())
                .userId(groupmember.getUser().getId())
                .userName(groupmember.getUser().getName())
                .groupId(groupmember.getGroup().getId())
                .groupName(groupmember.getGroup().getName())
                .joinedAt(groupmember.getJoinedAt())
                .build();
    }
}
