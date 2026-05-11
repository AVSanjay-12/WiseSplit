package com.sanjay.splitwise.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponseDTO {

    private Long id;

    private BigDecimal amount;

    private Long paidByUserId;
    private String paidByUserName;

    private Long groupId;
    private String groupName;

    private String description;

    private LocalDateTime createdAt;
}