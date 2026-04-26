package com.sanjay.splitwise.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SplitDTO {

    private Long userId;
    private BigDecimal shareAmount;
}