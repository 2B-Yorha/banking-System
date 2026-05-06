package com.Banking.system.controller;


import com.Banking.system.Service.interfaces.AccountService;
import com.Banking.system.dto.request.createAccountRequest;
import com.Banking.system.dto.response.AccountResponse;
import com.Banking.system.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@AuthenticationPrincipal UserDetails userDetails,
                                                                      @Valid @RequestBody createAccountRequest request){

        AccountResponse response =accountService.createAccount(userDetails.getUsername(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created Successfully", response));

    }



    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(@AuthenticationPrincipal UserDetails userDetails) {

        List<AccountResponse> accounts = accountService.getMyAccounts(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved", accounts));
    }



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @PathVariable Long id) {

        AccountResponse response = accountService.getAccountById(id, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.success("Account retrieved", response));
    }


    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AccountResponse>> deactivateAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        AccountResponse response = accountService.deactivateAccount(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Account deactivated", response));
    }



}
