package com.sanjay.splitwise.repository;

import com.sanjay.splitwise.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}