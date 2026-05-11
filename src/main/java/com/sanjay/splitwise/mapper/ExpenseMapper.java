package com.sanjay.splitwise.mapper;

import com.sanjay.splitwise.dto.ExpenseResponseDTO;
import com.sanjay.splitwise.entity.Expense;

public class ExpenseMapper {

    public static ExpenseResponseDTO toDTO(Expense expense) {

        return ExpenseResponseDTO.builder()
                .id(expense.getId())
                .amount(expense.getAmount())

                .paidByUserId(expense.getPaidBy().getId())
                .paidByUserName(expense.getPaidBy().getName())

                .groupId(expense.getGroup().getId())
                .groupName(expense.getGroup().getName())

                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}