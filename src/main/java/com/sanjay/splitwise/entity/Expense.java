package com.sanjay.splitwise.entity;

import com.sanjay.splitwise.entity.base.BaseEntity;
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
    @JoinColumn(name = "paid_by", nullable = false)
    private User paidBy;

    // Which group
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private String description;
}