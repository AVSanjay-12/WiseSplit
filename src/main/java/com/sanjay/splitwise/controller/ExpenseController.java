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
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
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
    public ExpenseResponseDTO addExpense(@Valid @RequestBody ExpenseRequestDTO request,
                                         Authentication authentication) {
        String email = authentication.getName();
        Expense expense =  expenseService.addExpense(request, email);

        return ExpenseMapper.toDTO(expense);
    }

    @GetMapping("/groups/{groupId}/balances")
    @Operation(summary = "Get balances of the group")
    public Map<Long, BigDecimal> calculateBalances(@PathVariable Long groupId, Authentication authentication){

        String email = authentication.getName();
        return expenseService.calculateBalances(groupId, email);
    }

    @GetMapping("/groups/{groupId}/settlements")
    @Operation(summary = "Get Settlement details")
    public List<SettlementResponseDTO> calculateSettlements(@PathVariable Long groupId, Authentication authentication) {

        String email = authentication.getName();

        return expenseService.calculateSettlements(groupId, email);
    }

    @PostMapping("/settle")
    public ExpenseResponseDTO settleUp(@RequestBody SettleUpRequestDTO request,
                                       Authentication authentication) {

        String email = authentication.getName();

        Expense expense = expenseService.settleUp(request, email);

        return ExpenseMapper.toDTO(expense);
    }

    @GetMapping("/groups/{groupId}")
    @Operation(summary = "Group expense history")
    public Page<ExpenseResponseDTO> getGroupExpenses(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "5")
            int size,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return expenseService.getGroupExpenses(groupId, page, size, email);
    }

    @GetMapping("/users/my-expenses")
    @Operation(summary = "User expense history")
    public Page<ExpenseResponseDTO> getUserExpenses(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "5")
            int size,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return expenseService.getUserExpenses(page, size, email);
    }
}