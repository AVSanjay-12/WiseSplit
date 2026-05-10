package com.sanjay.splitwise.service;

import com.sanjay.splitwise.entity.Group;
import com.sanjay.splitwise.entity.GroupMember;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.repository.GroupMemberRepository;
import com.sanjay.splitwise.repository.GroupRepository;
import com.sanjay.splitwise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        UserRepository userRepository) {

        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    // Create Group
    public Group createGroup(String name) {

        Group group = Group.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();

        return groupRepository.save(group);
    }

    // Add User to Group
    public GroupMember addUserToGroup(Long userId, Long groupId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupMember groupMember = GroupMember.builder()
                .user(user)
                .group(group)
                .joinedAt(LocalDateTime.now())
                .build();

        groupMember.setUser(user);
        groupMember.setGroup(group);
        groupMember.setJoinedAt(LocalDateTime.now());

        return groupMemberRepository.save(groupMember);
    }
}