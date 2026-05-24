package com.sanjay.splitwise.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettleUpRequestDTO {

    private Long payerUserId;

    private Long receiverUserId;

    private Long groupId;

    private BigDecimal amount;
}