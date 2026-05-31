package com.sanjay.splitwise.service;

import com.sanjay.splitwise.entity.Group;
import com.sanjay.splitwise.entity.GroupMember;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.exception.AlreadyExistsException;
import com.sanjay.splitwise.exception.ResourceNotFoundException;
import com.sanjay.splitwise.exception.UnauthorizedActionException;
import com.sanjay.splitwise.repository.GroupMemberRepository;
import com.sanjay.splitwise.repository.GroupRepository;
import com.sanjay.splitwise.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

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
                .orElseThrow(() -> {

                    logUserNotFound(email);

                    return new ResourceNotFoundException(
                            "User not found"
                    );
                });

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

        log.info(
                "Group created successfully. groupId={}, groupName={}, creator={}",
                savedGroup.getId(),
                savedGroup.getName(),
                email
        );

        return savedGroup;
    }

    // Add User to Group
    public GroupMember addUserToGroup(Long userId, Long groupId, String email) {

        validateMemberBelongToGroup(email, groupId);

        boolean alreadyMember = groupMemberRepository
                .existsByUser_IdAndGroup_Id(
                        userId,
                        groupId
                );

        if (alreadyMember) {

            log.warn(
                    "Attempt to add existing member. userId={}, groupId={}",
                    userId,
                    groupId
            );

            throw new AlreadyExistsException(
                    "User already belongs to group"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->{

                    logUserNotFound(email);

                    return new ResourceNotFoundException("User not found");
                });

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->{

                    log.warn(
                            "Group not found. groupId={}",
                            groupId
                    );

                    return new ResourceNotFoundException("Group not found");
                });

        GroupMember groupMember = GroupMember.builder()
                .user(user)
                .group(group)
                .joinedAt(LocalDateTime.now())
                .build();

        log.info(
                "User {} added to group {}",
                userId,
                groupId
        );

        return groupMemberRepository.save(groupMember);
    }

    private void validateMemberBelongToGroup(String email, Long groupId) {

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(() ->{

                    logUserNotFound(email);

                    return new ResourceNotFoundException(
                            "User not found"
                    );
                });

        boolean isMember = groupMemberRepository
                .existsByUser_IdAndGroup_Id(
                        currentUser.getId(),
                        groupId
                );

        if (!isMember) {

            log.warn(
                    "Unauthorized group access. userEmail={}, groupId={}",
                    email,
                    groupId
            );

            throw new UnauthorizedActionException(
                    "You are not a member of this group"
            );
        }
    }

    private void logUserNotFound(String email){
        log.warn("User with email {} not found", email);
    }
}