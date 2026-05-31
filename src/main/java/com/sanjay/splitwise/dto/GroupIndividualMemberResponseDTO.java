package com.sanjay.splitwise.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupIndividualMemberResponseDTO {

    private Long userId;

    private String userName;

    private String email;

    private LocalDateTime joinedAt;
}