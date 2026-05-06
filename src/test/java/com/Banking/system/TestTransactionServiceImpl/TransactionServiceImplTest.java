package com.Banking.system.TestTransactionServiceImpl;



import com.Banking.system.dto.request.DepositRequest;
import com.Banking.system.dto.request.TransferRequest;
import com.Banking.system.dto.request.WithdrawRequest;
import com.Banking.system.dto.response.TransactionResponse;
import com.Banking.system.entity.Account;
import com.Banking.system.entity.Transaction;
import com.Banking.system.entity.User;
import com.Banking.system.enums.AccountStatus;
import com.Banking.system.enums.AccountType;
import com.Banking.system.enums.Role;
import com.Banking.system.enums.TransactionStatus;
import com.Banking.system.enums.TransactionType;
import com.Banking.system.exception.AccountNotActiveException;
import com.Banking.system.exception.InsufficientFundsException;
import com.Banking.system.exception.ResourceNotFoundException;
import com.Banking.system.exception.UnauthorizedAccessException;
import com.Banking.system.repository.AccountRepository;
import com.Banking.system.repository.TransactionRepository;
import com.Banking.system.repository.UserRepository;
import com.Banking.system.Service.implementations.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private TransactionServiceImpl transactionService;

    private User user;
    private User otherUser;
    private Account account;
    private Account toAccount;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).firstName("John").lastName("Doe")
                .email("john@example.com").role(Role.USER).enabled(true)
                .build();

        otherUser = User.builder()
                .id(2L).firstName("Jane").lastName("Smith")
                .email("jane@example.com").role(Role.USER).enabled(true)
                .build();

        account = Account.builder()
                .id(1L).accountNumber("100000000001")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        toAccount = Account.builder()
                .id(2L).accountNumber("100000000002")
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(500))
                .status(AccountStatus.ACTIVE)
                .user(otherUser)
                .build();

        transaction = Transaction.builder()
                .id(1L).referenceNumber("ref-001")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(200))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1200))
                .status(TransactionStatus.COMPLETED)
                .account(account)
                .build();
    }

    // ---- Deposit -------------------------------------------------------------

    @Test
    void deposit_Success() {
        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(BigDecimal.valueOf(200));
        request.setDescription("Test deposit");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.deposit("john@example.com", request);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(TransactionType.DEPOSIT);
        verify(accountRepository).save(argThat(a ->
                a.getBalance().compareTo(BigDecimal.valueOf(1200)) == 0));
    }

    @Test
    void deposit_WrongOwner_ThrowsException() {
        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(BigDecimal.valueOf(200));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.deposit("other@example.com", request))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void deposit_AccountNotActive_ThrowsException() {
        account.setStatus(AccountStatus.FROZEN);
        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(BigDecimal.valueOf(200));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.deposit("john@example.com", request))
                .isInstanceOf(AccountNotActiveException.class);
    }

    // ---- Withdraw ------------------------------------------------------------

    @Test
    void withdraw_Success() {
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(BigDecimal.valueOf(500));
        request.setDescription("Test withdrawal");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.withdraw("john@example.com", request);

        assertThat(response).isNotNull();
        verify(accountRepository).save(argThat(a ->
                a.getBalance().compareTo(BigDecimal.valueOf(500)) == 0));
    }

    @Test
    void withdraw_InsufficientFunds_ThrowsException() {
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(BigDecimal.valueOf(9999));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdraw("john@example.com", request))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_AccountNotActive_ThrowsException() {
        account.setStatus(AccountStatus.CLOSED);
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdraw("john@example.com", request))
                .isInstanceOf(AccountNotActiveException.class);
    }

    @Test
    void withdraw_WrongOwner_ThrowsException() {
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdraw("other@example.com", request))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    // ---- Transfer ------------------------------------------------------------

    @Test
    void transfer_Success() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("100000000002");
        request.setAmount(BigDecimal.valueOf(300));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("100000000002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.transfer("john@example.com", request);

        assertThat(response).isNotNull();
        // two saves: one for each account
        verify(accountRepository, times(2)).save(any(Account.class));
        // two transaction rows: TRANSFER_OUT + TRANSFER_IN
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("100000000002");
        request.setAmount(BigDecimal.valueOf(9999));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("100000000002")).thenReturn(Optional.of(toAccount));

        assertThatThrownBy(() -> transactionService.transfer("john@example.com", request))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void transfer_SameAccount_ThrowsException() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("100000000001");
        request.setAmount(BigDecimal.valueOf(100));

        // make toAccount the same as fromAccount
        toAccount.setId(1L);
        toAccount.setAccountNumber("100000000001");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("100000000001")).thenReturn(Optional.of(toAccount));

        assertThatThrownBy(() -> transactionService.transfer("john@example.com", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void transfer_DestinationNotFound_ThrowsException() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("999999999999");
        request.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("999999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.transfer("john@example.com", request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void transfer_FrozenDestination_ThrowsException() {
        toAccount.setStatus(AccountStatus.FROZEN);
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountNumber("100000000002");
        request.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("100000000002")).thenReturn(Optional.of(toAccount));

        assertThatThrownBy(() -> transactionService.transfer("john@example.com", request))
                .isInstanceOf(AccountNotActiveException.class);
    }

    // ---- getTransactionByReference -------------------------------------------

    @Test
    void getTransactionByReference_Success() {
        when(transactionRepository.findByReferenceNumber("ref-001")).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getTransactionByReference("ref-001", "john@example.com");

        assertThat(response.getReferenceNumber()).isEqualTo("ref-001");
    }

    @Test
    void getTransactionByReference_NotFound_ThrowsException() {
        when(transactionRepository.findByReferenceNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionByReference("bad-ref", "john@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getTransactionByReference_WrongOwner_ThrowsException() {
        when(transactionRepository.findByReferenceNumber("ref-001")).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.getTransactionByReference("ref-001", "other@example.com"))
                .isInstanceOf(UnauthorizedAccessException.class);
    }



}
