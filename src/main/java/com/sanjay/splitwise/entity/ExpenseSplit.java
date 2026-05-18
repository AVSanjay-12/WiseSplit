package com.sanjay.splitwise.entity;

import com.sanjay.splitwise.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_splits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplit extends BaseEntity {

    // Which expense
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    // Which user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // How much this user owes
    @Column(nullable = false)
    private BigDecimal shareAmount;
}