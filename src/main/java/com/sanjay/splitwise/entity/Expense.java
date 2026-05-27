package com.sanjay.splitwise.entity;

import com.sanjay.splitwise.entity.base.BaseEntity;
import com.sanjay.splitwise.enums.SplitType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense extends BaseEntity {

    // Total amount
    @Column(nullable = false)
    private BigDecimal amount;

    // Who paid
    @ManyToOne
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private User paidBy;

    // Which group
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Enumerated(EnumType.STRING)
    private SplitType splitType;

    private String description;
}