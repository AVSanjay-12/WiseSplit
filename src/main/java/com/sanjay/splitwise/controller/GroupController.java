package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.entity.Group;
import com.sanjay.splitwise.entity.GroupMember;
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
    public Group createGroup(@RequestParam String name) {
        return groupService.createGroup(name);
    }

    // Add user to group
    // POST /groups/{groupId}/users/{userId}
    @PostMapping("/{groupId}/users/{userId}")
    public GroupMember addUserToGroup(@PathVariable Long groupId,
                                      @PathVariable Long userId) {

        return groupService.addUserToGroup(userId, groupId);
    }
}