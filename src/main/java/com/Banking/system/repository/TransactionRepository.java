package com.Banking.system.repository;


import com.Banking.system.entity.Transaction;
import com.Banking.system.enums.TransactionType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    List<Transaction> findByAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long accountId,
            LocalDateTime from,
            LocalDateTime to
    );


    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findAllByUserId(@Param("userId") Long userId, Pageable pageable);


    long countByType(TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :from AND :to ORDER BY t.createdAt DESC")
    List<Transaction> findAllInDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);


}
