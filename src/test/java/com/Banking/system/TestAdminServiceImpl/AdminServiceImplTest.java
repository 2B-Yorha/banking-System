package com.Banking.system.TestAdminServiceImpl;




import com.Banking.system.dto.response.AdminDashboardResponse;
import com.Banking.system.entity.Account;
import com.Banking.system.enums.AccountStatus;
import com.Banking.system.enums.TransactionType;
import com.Banking.system.repository.AccountRepository;
import com.Banking.system.repository.TransactionRepository;
import com.Banking.system.repository.UserRepository;
import com.Banking.system.Service.implementations.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private AdminServiceImpl adminService;

    // ---- getDashboardStats ---------------------------------------------------

    @Test
    void getDashboardStats_Success() {
        Account account1 = Account.builder().balance(BigDecimal.valueOf(1000)).build();
        Account account2 = Account.builder().balance(BigDecimal.valueOf(2500)).build();

        when(userRepository.count()).thenReturn(5L);
        when(accountRepository.count()).thenReturn(8L);
        when(accountRepository.countByStatus(AccountStatus.ACTIVE)).thenReturn(6L);
        when(transactionRepository.count()).thenReturn(20L);
        when(transactionRepository.countByType(TransactionType.DEPOSIT)).thenReturn(10L);
        when(transactionRepository.countByType(TransactionType.WITHDRAWAL)).thenReturn(5L);
        when(transactionRepository.countByType(TransactionType.TRANSFER_OUT)).thenReturn(5L);
        when(accountRepository.findAll()).thenReturn(List.of(account1, account2));

        AdminDashboardResponse response = adminService.getDashboardStats();

        assertThat(response).isNotNull();
        assertThat(response.getTotalUsers()).isEqualTo(5L);
        assertThat(response.getTotalAccounts()).isEqualTo(8L);
        assertThat(response.getActiveAccounts()).isEqualTo(6L);
        assertThat(response.getTotalTransactions()).isEqualTo(20L);
        assertThat(response.getTotalDeposits()).isEqualTo(10L);
        assertThat(response.getTotalWithdrawals()).isEqualTo(5L);
        assertThat(response.getTotalTransfers()).isEqualTo(5L);
        assertThat(response.getTotalSystemBalance()).isEqualByComparingTo(BigDecimal.valueOf(3500));
    }

    @Test
    void getDashboardStats_NoAccounts_ReturnsZeroBalance() {
        when(userRepository.count()).thenReturn(0L);
        when(accountRepository.count()).thenReturn(0L);
        when(accountRepository.countByStatus(AccountStatus.ACTIVE)).thenReturn(0L);
        when(transactionRepository.count()).thenReturn(0L);
        when(transactionRepository.countByType(any())).thenReturn(0L);
        when(accountRepository.findAll()).thenReturn(List.of());

        AdminDashboardResponse response = adminService.getDashboardStats();

        assertThat(response.getTotalSystemBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getTotalUsers()).isEqualTo(0L);
    }

    @Test
    void getDashboardStats_NullBalances_HandledGracefully() {
        Account accountWithNullBalance = Account.builder().balance(null).build();

        when(userRepository.count()).thenReturn(1L);
        when(accountRepository.count()).thenReturn(1L);
        when(accountRepository.countByStatus(AccountStatus.ACTIVE)).thenReturn(1L);
        when(transactionRepository.count()).thenReturn(0L);
        when(transactionRepository.countByType(any())).thenReturn(0L);
        when(accountRepository.findAll()).thenReturn(List.of(accountWithNullBalance));

        AdminDashboardResponse response = adminService.getDashboardStats();

        // null balance should be treated as zero — no NPE
        assertThat(response.getTotalSystemBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getDashboardStats_CallsAllRepositories() {
        when(accountRepository.findAll()).thenReturn(List.of());

        adminService.getDashboardStats();

        verify(userRepository).count();
        verify(accountRepository).count();
        verify(accountRepository).countByStatus(AccountStatus.ACTIVE);
        verify(transactionRepository).count();
        verify(transactionRepository).countByType(TransactionType.DEPOSIT);
        verify(transactionRepository).countByType(TransactionType.WITHDRAWAL);
        verify(transactionRepository).countByType(TransactionType.TRANSFER_OUT);
        verify(accountRepository).findAll();
    }


}
