package com.sanjay.splitwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettleUpRequestDTO {

    @NotNull(message = "Receiver user ID is required")
    private Long receiverUserId;

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than 0"
    )
    private BigDecimal amount;
}