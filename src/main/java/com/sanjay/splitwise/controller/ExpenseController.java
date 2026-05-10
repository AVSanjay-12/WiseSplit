package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.ExpenseRequestDTO;
import com.sanjay.splitwise.entity.Expense;
import com.sanjay.splitwise.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public Expense addExpense(@RequestBody ExpenseRequestDTO request) {
        return expenseService.addExpense(request);
    }
}