package com.Banking.system.repository;

import com.Banking.system.entity.Account;
import com.Banking.system.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long userId);
    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    long countByStatus(AccountStatus status);


}
