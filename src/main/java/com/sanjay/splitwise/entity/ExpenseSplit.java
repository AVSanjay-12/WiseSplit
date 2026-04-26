package com.sanjay.splitwise.entity;

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
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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