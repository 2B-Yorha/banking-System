package com.Banking.system.Service.implementations;


import com.Banking.system.Service.interfaces.TransactionService;
import com.Banking.system.dto.request.DepositRequest;
import com.Banking.system.dto.request.TransferRequest;
import com.Banking.system.dto.request.WithdrawRequest;
import com.Banking.system.dto.response.TransactionResponse;
import com.Banking.system.entity.Account;
import com.Banking.system.entity.Transaction;
import com.Banking.system.entity.User;
import com.Banking.system.enums.AccountStatus;
import com.Banking.system.enums.TransactionStatus;
import com.Banking.system.enums.TransactionType;
import com.Banking.system.exception.AccountNotActiveException;
import com.Banking.system.exception.InsufficientFundsException;
import com.Banking.system.exception.ResourceNotFoundException;
import com.Banking.system.exception.UnauthorizedAccessException;
import com.Banking.system.repository.AccountRepository;
import com.Banking.system.repository.TransactionRepository;
import com.Banking.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public TransactionResponse deposit(String email, DepositRequest request) {
        Account account = findAccountById(request.getAccountId());
        assertOwnership(account, email);
        assertActive(account);

        BigDecimal before = account.getBalance();
        BigDecimal after  = before.add(request.getAmount());

        account.setBalance(after);
        accountRepository.save(account);

        Transaction txn = buildTransaction(
                TransactionType.DEPOSIT, request.getAmount(),
                before, after, request.getDescription(),
                account, null
        );

        return mapToResponse(transactionRepository.save(txn));
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(String email, WithdrawRequest request) {
        Account account = findAccountById(request.getAccountId());
        assertOwnership(account, email);
        assertActive(account);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + account.getBalance());
        }

        BigDecimal before = account.getBalance();
        BigDecimal after  = before.subtract(request.getAmount());

        account.setBalance(after);
        accountRepository.save(account);

        Transaction txn = buildTransaction(
                TransactionType.WITHDRAWAL, request.getAmount(),
                before, after, request.getDescription(),
                account, null
        );

        return mapToResponse(transactionRepository.save(txn));
    }

    @Override
    @Transactional
    public TransactionResponse transfer(String email, TransferRequest request) {
        Account fromAccount = findAccountById(request.getFromAccountId());
        assertOwnership(fromAccount, email);
        assertActive(fromAccount);

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destination account not found: " + request.getToAccountNumber()));
        assertActive(toAccount);

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + fromAccount.getBalance());
        }

        // Debit sender
        BigDecimal fromBefore = fromAccount.getBalance();
        BigDecimal fromAfter  = fromBefore.subtract(request.getAmount());
        fromAccount.setBalance(fromAfter);
        accountRepository.save(fromAccount);

        // Credit receiver
        BigDecimal toBefore = toAccount.getBalance();
        BigDecimal toAfter  = toBefore.add(request.getAmount());
        toAccount.setBalance(toAfter);
        accountRepository.save(toAccount);

        String ref = UUID.randomUUID().toString();

        // Sender leg
        Transaction outTxn = buildTransactionWithRef(
                ref, TransactionType.TRANSFER_OUT, request.getAmount(),
                fromBefore, fromAfter, request.getDescription(),
                fromAccount, toAccount
        );
        transactionRepository.save(outTxn);

        // Receiver leg
        Transaction inTxn = buildTransactionWithRef(
                UUID.randomUUID().toString(), TransactionType.TRANSFER_IN, request.getAmount(),
                toBefore, toAfter, request.getDescription(),
                toAccount, fromAccount
        );
        transactionRepository.save(inTxn);

        // Return the sender's leg
        return mapToResponse(outTxn);
    }

    @Override
    public Page<TransactionResponse> getAccountTransactions(Long accountId, String email, Pageable pageable) {
        Account account = findAccountById(accountId);
        assertOwnership(account, email);
        return transactionRepository
                .findByAccountIdOrderByCreatedAtDesc(accountId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<TransactionResponse> getMyTransactions(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return transactionRepository
                .findAllByUserId(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    public TransactionResponse getTransactionByReference(String reference, String email) {
        Transaction txn = transactionRepository.findByReferenceNumber(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + reference));
        assertOwnership(txn.getAccount(), email);
        return mapToResponse(txn);
    }

    @Override
    public List<TransactionResponse> getBalanceHistory(Long accountId, String email,
                                                       LocalDateTime from, LocalDateTime to) {
        Account account = findAccountById(accountId);
        assertOwnership(account, email);
        return transactionRepository
                .findByAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(accountId, from, to)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<TransactionResponse> getAllTransactionsInRange(LocalDateTime from, LocalDateTime to) {
        return transactionRepository.findAllInDateRange(from, to)
                .stream().map(this::mapToResponse).toList();
    }



    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    private void assertOwnership(Account account, String email) {
        if (!account.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You do not own this account");
        }
    }

    private void assertActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Account " + account.getAccountNumber() + " is " + account.getStatus());
        }
    }

    private Transaction buildTransaction(TransactionType type, BigDecimal amount,
                                         BigDecimal before, BigDecimal after,
                                         String description, Account account, Account related) {
        return buildTransactionWithRef(
                UUID.randomUUID().toString(), type, amount, before, after, description, account, related);
    }

    private Transaction buildTransactionWithRef(String ref, TransactionType type, BigDecimal amount,
                                                BigDecimal before, BigDecimal after, String description,
                                                Account account, Account related) {
        return Transaction.builder()
                .referenceNumber(ref)
                .type(type)
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(after)
                .description(description)
                .status(TransactionStatus.COMPLETED)
                .account(account)
                .relatedAccount(related)
                .build();
    }

    public TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .referenceNumber(t.getReferenceNumber())
                .type(t.getType())
                .amount(t.getAmount())
                .balanceBefore(t.getBalanceBefore())
                .balanceAfter(t.getBalanceAfter())
                .description(t.getDescription())
                .status(t.getStatus())
                .accountId(t.getAccount().getId())
                .accountNumber(t.getAccount().getAccountNumber())
                .relatedAccountId(t.getRelatedAccount() != null ? t.getRelatedAccount().getId() : null)
                .relatedAccountNumber(t.getRelatedAccount() != null ? t.getRelatedAccount().getAccountNumber() : null)
                .createdAt(t.getCreatedAt())
                .build();
    }





}
