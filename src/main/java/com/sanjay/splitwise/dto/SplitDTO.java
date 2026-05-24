package com.sanjay.splitwise.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitDTO {

    private Long userId;
    private BigDecimal shareAmount;
    private BigDecimal percentage;
}