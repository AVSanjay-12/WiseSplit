package com.sanjay.splitwise.controller;

import com.sanjay.splitwise.dto.ExpenseRequestDTO;
import com.sanjay.splitwise.dto.ExpenseResponseDTO;
import com.sanjay.splitwise.dto.SettleUpRequestDTO;
import com.sanjay.splitwise.dto.SettlementResponseDTO;
import com.sanjay.splitwise.entity.Expense;
import com.sanjay.splitwise.mapper.ExpenseMapper;
import com.sanjay.splitwise.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @Operation(summary = "Create a new expense")
    public ExpenseResponseDTO addExpense(@Valid @RequestBody ExpenseRequestDTO request) {
        Expense expense =  expenseService.addExpense(request);

        return ExpenseMapper.toDTO(expense);
    }

    @GetMapping("/groups/{groupId}/balances")
    @Operation(summary = "Get balances of the group")
    public Map<Long, BigDecimal> calculateBalances(@PathVariable Long groupId){
        return expenseService.calculateBalances(groupId);
    }

    @GetMapping("/groups/{groupId}/settlements")
    @Operation(summary = "Get Settlement details")
    public List<SettlementResponseDTO> calculateSettlements(@PathVariable Long groupId) {

        return expenseService.calculateSettlements(groupId);
    }

    @PostMapping("/settle")
    public ExpenseResponseDTO settleUp(@RequestBody SettleUpRequestDTO request) {

        Expense expense = expenseService.settleUp(request);

        return ExpenseMapper.toDTO(expense);
    }
}