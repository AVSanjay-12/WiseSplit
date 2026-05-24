package com.sanjay.splitwise.repository;

import com.sanjay.splitwise.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroupId(Long groupId);

    Page<Expense> findByGroupId(
            Long groupId,
            Pageable pageable
    );

    Page<Expense> findByPaidById(
            Long userId,
            Pageable pageable
    );
}