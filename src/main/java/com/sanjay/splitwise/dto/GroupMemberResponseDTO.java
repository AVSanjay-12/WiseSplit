package com.sanjay.splitwise.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long groupId;
    private String groupName;
    private LocalDateTime joinedAt;
}
