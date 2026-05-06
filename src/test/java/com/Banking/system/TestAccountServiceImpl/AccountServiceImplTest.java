package com.Banking.system.TestAccountServiceImpl;

import com.Banking.system.dto.request.createAccountRequest;
import com.Banking.system.dto.response.AccountResponse;
import com.Banking.system.entity.Account;
import com.Banking.system.entity.User;
import com.Banking.system.enums.AccountStatus;
import com.Banking.system.enums.AccountType;
import com.Banking.system.enums.Role;
import com.Banking.system.exception.ResourceNotFoundException;
import com.Banking.system.exception.UnauthorizedAccessException;
import com.Banking.system.repository.AccountRepository;
import com.Banking.system.repository.UserRepository;
import com.Banking.system.Service.implementations.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {


    @Mock private AccountRepository accountRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private AccountServiceImpl accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Lionel")
                .lastName("Messi")
                .email("lmessi10@gmail.com")
                .role(Role.USER)
                .enabled(true)
                .build();

        account = Account.builder()
                .id(1L)
                .accountNumber("100000000001")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();
    }

    //creating an account test:
    @Test
    void createAccount_Success() {
        createAccountRequest request = new createAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        when(userRepository.findByEmail("lmessi10@gmail.com")).thenReturn(Optional.of(user));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.createAccount("lmessi10@gmail.com", request);

        assertThat(response).isNotNull();
        assertThat(response.getAccountType()).isEqualTo(AccountType.SAVINGS);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createAccount("lmessi10@gmail.com", new createAccountRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }




    //Getting My Account Tests:
    @Test
    void getMyAccounts_Success() {
        when(userRepository.findByEmail("lmessi10@gmail.com")).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));

        List<AccountResponse> accounts = accountService.getMyAccounts("lmessi10@gmail.com");

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountNumber()).isEqualTo("100000000001");
    }

    @Test
    void getMyAccounts_ReturnsEmpty_WhenNoAccounts() {
        when(userRepository.findByEmail("lmessi10@gmail.com")).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(1L)).thenReturn(List.of());

        List<AccountResponse> accounts = accountService.getMyAccounts("lmessi10@gmail.com");

        assertThat(accounts).isEmpty();
    }


    //get my account by Id
    @Test
    void getAccountById_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccountById(1L, "lmessi10@gmail.com");

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void getAccountById_WrongOwner_ThrowsException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.getAccountById(1L, "other@example.com"))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void getAccountById_NotFound_ThrowsException() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(99L, "lmessi10@gmail.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }



    //Admin tests:
    @Test
    void getAllAccounts_Success() {
        when(accountRepository.findAll()).thenReturn(List.of(account));

        List<AccountResponse> accounts = accountService.getAllAccounts();

        assertThat(accounts).hasSize(1);
    }

    @Test
    void updateAccountStatus_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        accountService.updateAccountStatus(1L, AccountStatus.FROZEN);

        verify(accountRepository).save(argThat(a -> a.getStatus() == AccountStatus.FROZEN));
    }




}
