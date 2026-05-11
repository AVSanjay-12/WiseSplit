package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.GroupMemberResponseDTO;
import com.sanjay.splitwise.dto.GroupResponseDTO;
import com.sanjay.splitwise.entity.Group;
import com.sanjay.splitwise.entity.GroupMember;
import com.sanjay.splitwise.mapper.GroupMapper;
import com.sanjay.splitwise.mapper.GroupMemberMapper;
import com.sanjay.splitwise.service.GroupService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // Create group
    // POST /groups?name=Trip
    @PostMapping
    public GroupResponseDTO createGroup(@RequestParam String name) {
        Group group = groupService.createGroup(name);
        return GroupMapper.toDTO(group);
    }

    // Add user to group
    // POST /groups/{groupId}/users/{userId}
    @PostMapping("/{groupId}/users/{userId}")
    public GroupMemberResponseDTO addUserToGroup(@PathVariable Long groupId,
                                                 @PathVariable Long userId) {

        GroupMember groupMember = groupService.addUserToGroup(userId, groupId);
        return GroupMemberMapper.toDTO(groupMember);
    }
}