package com.sanjay.splitwise.service;

import com.sanjay.splitwise.dto.ExpenseRequestDTO;
import com.sanjay.splitwise.dto.SplitDTO;
import com.sanjay.splitwise.entity.Expense;
import com.sanjay.splitwise.entity.ExpenseSplit;
import com.sanjay.splitwise.entity.Group;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.enums.SplitType;
import com.sanjay.splitwise.repository.ExpenseRepository;
import com.sanjay.splitwise.repository.ExpenseSplitRepository;
import com.sanjay.splitwise.repository.GroupRepository;
import com.sanjay.splitwise.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseSplitRepository expenseSplitRepository, UserRepository userRepository, GroupRepository groupRepository){
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public Expense addExpense(ExpenseRequestDTO request){

//        Fetch Payer
        User paidBy = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
//        Fetch group
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (request.getSplitType() == SplitType.EQUAL) {
            processEqualSplits(request);
        }

        else if (request.getSplitType() == SplitType.PERCENTAGE) {
            processPercentageSplits(request);
        }

//        Validate splits sum
        BigDecimal total = BigDecimal.ZERO;

        for(SplitDTO split: request.getSplits()){
            total = total.add(split.getShareAmount());
        }
        if(total.compareTo(request.getAmount()) != 0){
            throw new RuntimeException("Split amounts do not match total expense");
        }

//        Create Expense
        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .paidBy(paidBy)
                .group(group)
                .splitType(request.getSplitType())
                .description(request.getDescription())
                .build();

        Expense savedExpense = expenseRepository.save(expense);

//        Splits
        for(SplitDTO splitDTO: request.getSplits()){
            User user = userRepository.findById(splitDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ExpenseSplit split = ExpenseSplit.builder()
                    .expense(savedExpense)
                    .user(user)
                    .shareAmount(splitDTO.getShareAmount())
                    .build();

            expenseSplitRepository.save(split);
        }

        return savedExpense;
    }

    public Map<Long, BigDecimal> calculateBalances(Long groupId){
//        userId -> balance
        Map<Long, BigDecimal> balances = new HashMap<>();
//        Fetch all expenses in group
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);

//        Process each expense
        for(Expense expense: expenses){
            Long payerId = expense.getPaidBy().getId();
//            Add paid amount to payer
            balances.put(payerId,
                    balances.getOrDefault(payerId, BigDecimal.ZERO)
                            .add(expense.getAmount())
            );

            //        Fetch splits
            List<ExpenseSplit> splits = expenseSplitRepository.findByExpenseId(expense.getId());
            for(ExpenseSplit split: splits){
                Long userId = split.getUser().getId();

                balances.put(userId,
                        balances.getOrDefault(userId, BigDecimal.ZERO)
                                .subtract(split.getShareAmount()));
            }
        }
        return balances;
    }

    private void processEqualSplits(ExpenseRequestDTO request){

        int totalUsers = request.getSplits().size();

        BigDecimal totalAmount = request.getAmount();

        BigDecimal equalShare = totalAmount.divide(
                BigDecimal.valueOf(totalUsers),
                2,
                RoundingMode.HALF_UP
        );

        BigDecimal distributedAmount = BigDecimal.ZERO;

        for(int i=0; i<totalUsers; i++){
            if(i == totalUsers-1){

                BigDecimal remainingAmount = totalAmount.subtract(distributedAmount);

                request.getSplits().get(i).setShareAmount(remainingAmount);
            }
            else{
                request.getSplits().get(i).setShareAmount(equalShare);

                distributedAmount = distributedAmount.add(equalShare);
            }
        }
    }

    private void processPercentageSplits(ExpenseRequestDTO request){

        BigDecimal totalPercentage = BigDecimal.ZERO;

        for(SplitDTO split: request.getSplits()){
            if(split.getPercentage() == null){
                throw new RuntimeException("Percentage is required for PERCENTAGE split");
            }
            totalPercentage =  totalPercentage.add(split.getPercentage());
        }

        if(totalPercentage.compareTo(BigDecimal.valueOf(100.00)) != 0){
            throw new RuntimeException("Total percentage must equal 100");
        }

        BigDecimal totalAmount = request.getAmount();

        BigDecimal distributedAmount = BigDecimal.ZERO;

        int totalUsers = request.getSplits().size();

        for(int i=0; i<totalUsers; i++){

            SplitDTO split = request.getSplits().get(i);

            if(i == totalUsers - 1) {
                BigDecimal remainingAmount = totalAmount.subtract(distributedAmount);

                split.setShareAmount(remainingAmount);
            }
            else{
                BigDecimal shareAmount = totalAmount
                        .multiply(split.getPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                split.setShareAmount(shareAmount);

                distributedAmount = distributedAmount.add(shareAmount);
            }
        }
    }
}
