package com.sanjay.splitwise.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponseDTO {

    private Long fromUserId;
    private String fromUserName;

    private Long toUserId;
    private String toUserName;

    private BigDecimal amount;
}