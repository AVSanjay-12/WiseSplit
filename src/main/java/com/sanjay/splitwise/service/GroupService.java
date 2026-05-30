package com.sanjay.splitwise.service;

import com.sanjay.splitwise.entity.Group;
import com.sanjay.splitwise.entity.GroupMember;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.exception.ResourceNotFoundException;
import com.sanjay.splitwise.exception.UnauthorizedActionException;
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
    public Group createGroup(String name, String email) {

        User creator = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Group group = Group.builder()
                .name(name)
                .build();

        Group savedGroup = groupRepository.save(group);

        GroupMember groupMember = GroupMember.builder()
                .user(creator)
                .group(savedGroup)
                .joinedAt(LocalDateTime.now())
                .build();

        groupMemberRepository.save(groupMember);

        return savedGroup;
    }

    // Add User to Group
    public GroupMember addUserToGroup(Long userId, Long groupId, String email) {

        validateMemberBelongToGroup(email, groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        boolean alreadyMember = groupMemberRepository
                .existsByUser_IdAndGroup_Id(
                        userId,
                        groupId
                );

        if (alreadyMember) {

            throw new UnauthorizedActionException(
                    "User already belongs to group"
            );
        }

        GroupMember groupMember = GroupMember.builder()
                .user(user)
                .group(group)
                .joinedAt(LocalDateTime.now())
                .build();

        return groupMemberRepository.save(groupMember);
    }

    private void validateMemberBelongToGroup(String email, Long groupId) {

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        boolean isMember = groupMemberRepository
                .existsByUser_IdAndGroup_Id(
                        currentUser.getId(),
                        groupId
                );

        if (!isMember) {
            throw new UnauthorizedActionException(
                    "You are not a member of this group"
            );
        }
    }
}