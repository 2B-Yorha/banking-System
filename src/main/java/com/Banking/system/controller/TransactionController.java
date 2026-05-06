package com.Banking.system.controller;


import com.Banking.system.Service.interfaces.TransactionService;
import com.Banking.system.dto.request.DepositRequest;
import com.Banking.system.dto.request.TransferRequest;
import com.Banking.system.dto.request.WithdrawRequest;
import com.Banking.system.dto.response.ApiResponse;
import com.Banking.system.dto.response.TransactionResponse;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DepositRequest request) {
        TransactionResponse response = transactionService.deposit(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful", response));
    }




    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WithdrawRequest request) {
        TransactionResponse response = transactionService.withdraw(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful", response));
    }



    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer successful", response));
    }



    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionResponse> transactions =
                transactionService.getMyTransactions(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", transactions));
    }




    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAccountTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionResponse> transactions =
                transactionService.getAccountTransactions(accountId, userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", transactions));
    }



    @GetMapping("/reference/{ref}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getByReference(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String ref) {
        TransactionResponse response =
                transactionService.getTransactionByReference(ref, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved", response));
    }





    @GetMapping("/account/{accountId}/history")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getBalanceHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<TransactionResponse> history =
                transactionService.getBalanceHistory(accountId, userDetails.getUsername(), from, to);
        return ResponseEntity.ok(ApiResponse.success("Balance history retrieved", history));
    }




}
