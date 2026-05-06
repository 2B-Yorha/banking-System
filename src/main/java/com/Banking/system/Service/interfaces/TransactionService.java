package com.Banking.system.Service.interfaces;


import com.Banking.system.dto.request.DepositRequest;
import com.Banking.system.dto.request.TransferRequest;
import com.Banking.system.dto.request.WithdrawRequest;
import com.Banking.system.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    TransactionResponse deposit(String email, DepositRequest request);
    TransactionResponse withdraw(String email, WithdrawRequest request);
    TransactionResponse transfer(String email, TransferRequest request);

    Page<TransactionResponse> getAccountTransactions(Long accountId, String email, Pageable pageable);
    Page<TransactionResponse> getMyTransactions(String email, Pageable pageable);
    TransactionResponse getTransactionByReference(String reference, String email);


    List<TransactionResponse> getBalanceHistory(Long accountId, String email,
                                                LocalDateTime from, LocalDateTime to);


    List<TransactionResponse> getAllTransactionsInRange(LocalDateTime from, LocalDateTime to);

}
