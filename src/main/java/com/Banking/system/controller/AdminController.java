package com.Banking.system.controller;


import com.Banking.system.Service.interfaces.AccountService;
import com.Banking.system.Service.interfaces.AdminService;
import com.Banking.system.Service.interfaces.TransactionService;
import com.Banking.system.Service.interfaces.UserService;
import com.Banking.system.dto.response.*;
import com.Banking.system.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;


    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard(){
        return ResponseEntity.ok(ApiResponse.success("Dashbpard stats retrieved", adminService.getDashboardStats()));
    }



    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved", userService.getAllUsers()));
    }



    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("User retrieved", userService.getUserById(id)));
    }




    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.success("User status toggled", null));
    }



    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {
        return ResponseEntity.ok(
                ApiResponse.success("Accounts retrieved", accountService.getAllAccounts()));
    }




    @PatchMapping("/accounts/{id}/status")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(@PathVariable Long id,
                                                                            @RequestParam AccountStatus status) {
        AccountResponse response = accountService.updateAccountStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Account status updated", response));
    }




    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsInRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<TransactionResponse> transactions =
                transactionService.getAllTransactionsInRange(from, to);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", transactions));
    }





}
