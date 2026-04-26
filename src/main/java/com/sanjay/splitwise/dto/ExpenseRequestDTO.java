package com.sanjay.splitwise.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequestDTO {

    private BigDecimal amount;
    private Long paidByUserId;
    private Long groupId;
    private String description;

    private List<SplitDTO> splits;
}