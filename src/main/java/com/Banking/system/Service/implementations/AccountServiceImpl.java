package com.Banking.system.Service.implementations;

import com.Banking.system.dto.request.createAccountRequest;
import com.Banking.system.dto.response.AccountResponse;
import com.Banking.system.entity.Account;
import com.Banking.system.entity.User;
import com.Banking.system.enums.AccountStatus;
import com.Banking.system.exception.AccountNotActiveException;
import com.Banking.system.exception.ResourceNotFoundException;
import com.Banking.system.exception.UnauthorizedAccessException;
import com.Banking.system.repository.AccountRepository;
import com.Banking.system.repository.UserRepository;
import com.Banking.system.Service.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AccountResponse createAccount(String email, createAccountRequest request) {
        User user = findUserByEmail(email);

        //limits Account created to 5 for each user
        long accountCount = accountRepository.findByUserId(user.getId()).size();
        if (accountCount >= 5) {
            throw new IllegalStateException("Account limit reached. Maximum of 5 accounts allowed per user.");
        }

        Account account = Account.builder()
                .accountNumber(generateUniqueAccountNumber())
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        return mapToResponse(accountRepository.save(account));
    }


    @Override
    public List<AccountResponse> getMyAccounts(String email) {
        User user = findUserByEmail(email);
        return accountRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AccountResponse getAccountById(Long id, String email) {
        Account account = findById(id);
        assertOwnership(account, email);
        return mapToResponse(account);
    }





    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public AccountResponse updateAccountStatus(Long id, AccountStatus status) {
        Account account = findById(id);
        account.setStatus(status);
        return mapToResponse(accountRepository.save(account));
    }


    private Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void assertOwnership(Account account, String email) {
        if (!account.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You do not own this account");
        }
    }





    private String generateUniqueAccountNumber() {
        Random rng = new Random();
        String number;
        do {

            number = "10" + String.format("%010d", (long)(rng.nextDouble() * 10_000_000_000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }


    @Override
    @Transactional
    public AccountResponse deactivateAccount(Long id, String email) {

        Account account = findById(id);
        assertOwnership(account,email);
        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new AccountNotActiveException("Account is already " + account.getStatus());
        }
        account.setStatus(AccountStatus.INACTIVE);
        return mapToResponse(accountRepository.save(account));



    }

    public AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .userId(account.getUser().getId())
                .ownerFullName(account.getUser().getFirstName() + " " + account.getUser().getLastName())
                .createdAt(account.getCreatedAt())
                .build();

    }


}
