package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.ExpenseRequestDTO;
import com.sanjay.splitwise.dto.ExpenseResponseDTO;
import com.sanjay.splitwise.entity.Expense;
import com.sanjay.splitwise.mapper.ExpenseMapper;
import com.sanjay.splitwise.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ExpenseResponseDTO addExpense(@Valid @RequestBody ExpenseRequestDTO request) {
        Expense expense =  expenseService.addExpense(request);

        return ExpenseMapper.toDTO(expense);
    }

    @GetMapping("/groups/{groupId}/balances")
    public Map<Long, BigDecimal> calculateBalances(
            @PathVariable Long groupId
    ){
        return expenseService.calculateBalances(groupId);
    }
}