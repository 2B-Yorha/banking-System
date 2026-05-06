package com.Banking.system.Service.implementations;


import com.Banking.system.Service.interfaces.AdminService;
import com.Banking.system.dto.response.AdminDashboardResponse;
import com.Banking.system.enums.AccountStatus;
import com.Banking.system.enums.TransactionType;
import com.Banking.system.repository.AccountRepository;
import com.Banking.system.repository.TransactionRepository;
import com.Banking.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {


    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardStats() {

        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.countByStatus(AccountStatus.ACTIVE);
        long totalTransactions = transactionRepository.count();
        long totalDeposits = transactionRepository.countByType(TransactionType.DEPOSIT);
        long totalWithdrawals = transactionRepository.countByType(TransactionType.WITHDRAWAL);
        long totalTransfers = transactionRepository.countByType(TransactionType.TRANSFER_OUT);


        BigDecimal systemBalance = accountRepository.findAll().stream()
                .map(a -> a.getBalance() != null ? a.getBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .totalTransactions(totalTransactions)
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .totalTransfers(totalTransfers)
                .totalSystemBalance(systemBalance)
                .build();



    }
}
