package com.sanjay.splitwise.repository;

import com.sanjay.splitwise.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
}