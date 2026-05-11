package com.sanjay.splitwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequestDTO {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Paid by user id is required")
    private Long paidByUserId;

    @NotNull(message = "Group is required")
    private Long groupId;

    @NotBlank(message = "Description cannot be ")
    private String description;

    private List<SplitDTO> splits;
}