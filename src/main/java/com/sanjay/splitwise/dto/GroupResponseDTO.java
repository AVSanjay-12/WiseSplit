package com.sanjay.splitwise.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
