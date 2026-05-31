package com.sanjay.splitwise.service;

import com.sanjay.splitwise.dto.*;
import com.sanjay.splitwise.entity.Expense;
import com.sanjay.splitwise.entity.ExpenseSplit;
import com.sanjay.splitwise.entity.Group;
import com.sanjay.splitwise.entity.User;
import com.sanjay.splitwise.enums.SplitType;
import com.sanjay.splitwise.exception.InvalidSplitException;
import com.sanjay.splitwise.exception.ResourceNotFoundException;
import com.sanjay.splitwise.exception.UnauthorizedActionException;
import com.sanjay.splitwise.mapper.ExpenseMapper;
import com.sanjay.splitwise.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseSplitRepository expenseSplitRepository,
                          UserRepository userRepository, GroupRepository groupRepository,
                          GroupMemberRepository groupMemberRepository){
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional
    public Expense addExpense(@Valid @RequestBody ExpenseRequestDTO request, String email){

//        Fetch Payer
        User paidByUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//        Fetch group
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        validateGroupMembership(request, paidByUser.getId());

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
            throw new InvalidSplitException("Split amounts do not match total expense");
        }

//        Create Expense
        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .paidBy(paidByUser)
                .group(group)
                .splitType(request.getSplitType())
                .description(request.getDescription())
                .build();

        Expense savedExpense = expenseRepository.save(expense);

//        Splits
        for(SplitDTO splitDTO: request.getSplits()){
            User user = userRepository.findById(splitDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            ExpenseSplit split = ExpenseSplit.builder()
                    .expense(savedExpense)
                    .user(user)
                    .shareAmount(splitDTO.getShareAmount())
                    .build();

            expenseSplitRepository.save(split);
        }

        log.info(
                "Expense created in group {} by user {} amount {}",
                request.getGroupId(),
                paidByUser.getEmail(),
                request.getAmount()
        );

        return savedExpense;
    }

//    Calculate Balance
    public Map<Long, BigDecimal> calculateBalances(Long groupId, String email){

        validateMemberBelongToGroup(email, groupId);

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

    private void processEqualSplits(@Valid @RequestBody ExpenseRequestDTO request){

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

    private void processPercentageSplits(@Valid @RequestBody ExpenseRequestDTO request){

        BigDecimal totalPercentage = BigDecimal.ZERO;

        for(SplitDTO split: request.getSplits()){
            if(split.getPercentage() == null){
                throw new InvalidSplitException("Percentage is required for PERCENTAGE split");
            }
            totalPercentage =  totalPercentage.add(split.getPercentage());
        }

        if(totalPercentage.compareTo(BigDecimal.valueOf(100.00)) != 0){
            throw new InvalidSplitException("Total percentage must equal 100");
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

    public List<SettlementResponseDTO> calculateSettlements(Long groupId, String email){
        Map<Long, BigDecimal> balances = calculateBalances(groupId, email);

        Map<Long, BigDecimal> creditors = new HashMap<>();
        Map<Long, BigDecimal> debtors = new HashMap<>();

        for(Map.Entry<Long, BigDecimal> entry : balances.entrySet()) {
            if(entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.put(entry.getKey(), entry.getValue());
            } else if(entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.put(entry.getKey(), entry.getValue().abs());
            }
        }

        List<SettlementResponseDTO> settlements = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> debtorEntry : debtors.entrySet()) {

            Long debtorId = debtorEntry.getKey();

            BigDecimal debtAmount = debtorEntry.getValue();

            for (Map.Entry<Long, BigDecimal> creditorEntry : creditors.entrySet()) {

                if (debtAmount.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
                Long creditorId = creditorEntry.getKey();
                BigDecimal creditAmount = creditorEntry.getValue();

                if (creditAmount.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                BigDecimal settledAmount = debtAmount.min(creditAmount);

                // Settlement DTO
                User debtor = userRepository.findById(debtorId)
                                .orElseThrow();

                User creditor = userRepository.findById(creditorId)
                                .orElseThrow();

                settlements.add(SettlementResponseDTO.builder()
                                .fromUserId(debtorId)
                                .fromUserName(debtor.getName())
                                .toUserId(creditorId)
                                .toUserName(creditor.getName())
                                .amount(settledAmount)
                                .build()
                );

                debtAmount = debtAmount.subtract(settledAmount);

                creditorEntry.setValue(creditAmount.subtract(settledAmount));
            }
        }
        return settlements;
    }

    public Expense settleUp(SettleUpRequestDTO request, String email) {

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if (currentUser.getId().equals(request.getReceiverUserId())) {
            throw new InvalidSplitException(
                    "You cannot settle with yourself"
            );
        }

        SplitDTO splitDTO = SplitDTO.builder()
                .userId(request.getReceiverUserId())
                .shareAmount(request.getAmount())
                .build();

        ExpenseRequestDTO expenseRequest = ExpenseRequestDTO.builder()
                        .amount(request.getAmount())
                        .groupId(request.getGroupId())
                        .description("Settlement Payment")
                        .splitType(SplitType.EXACT)
                        .splits(List.of(splitDTO))
                        .build();

        log.info(
                "Settlement created from {} to {} amount {}",
                email,
                request.getReceiverUserId(),
                request.getAmount()
        );

        return addExpense(expenseRequest, email);
    }

    public Page<ExpenseResponseDTO> getGroupExpenses(Long groupId, int page, int size, String email) {

        validateMemberBelongToGroup(email, groupId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return expenseRepository
                .findByGroupId(groupId, pageable)
                .map(ExpenseMapper::toDTO);
    }

    public Page<ExpenseResponseDTO> getUserExpenses(int page, int size, String email) {

        User authenticatedUser = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedActionException(
                                "User not found"
                        )
                );
        Long userId = authenticatedUser.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return expenseRepository
                .findByPaidById(userId, pageable)
                .map(ExpenseMapper::toDTO);
    }

    private void validateSplitUsersBelongToGroup(ExpenseRequestDTO request) {

        for (SplitDTO split : request.getSplits()) {

            boolean isMember = groupMemberRepository.existsByUser_IdAndGroup_Id(
                                    split.getUserId(),
                                    request.getGroupId()
                            );

            if (!isMember) {
                throw new UnauthorizedActionException(
                        "All split users must belong to the group"
                );
            }
        }
    }

    private void validatePayerBelongsToGroup(Long payerUserId, Long groupId) {

        boolean isMember = groupMemberRepository.existsByUser_IdAndGroup_Id(payerUserId, groupId);

        if (!isMember) {

            log.warn(
                    "Unauthorized group payment attempt by user {} for group {}",
                    payerUserId,
                    groupId
            );

            throw new UnauthorizedActionException(
                    "Payer must belong to the group"
            );
        }
    }

    private void validateGroupMembership(ExpenseRequestDTO request, Long payerId) {
        validatePayerBelongsToGroup(
                payerId,
                request.getGroupId()
        );

        validateSplitUsersBelongToGroup(request);
    }

    private void validateMemberBelongToGroup(String email, Long groupId){
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        boolean isMember = groupMemberRepository
                .existsByUser_IdAndGroup_Id(
                        currentUser.getId(),
                        groupId
                );

        if (!isMember) {

            log.warn(
                    "Unauthorized group access attempt by user {} for group {}",
                    email,
                    groupId
            );

            throw new UnauthorizedActionException(
                    "You are not a member of this group"
            );
        }
    }
}
