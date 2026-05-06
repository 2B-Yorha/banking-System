package com.Banking.system.Service.interfaces;

import com.Banking.system.dto.request.createAccountRequest;
import com.Banking.system.dto.response.AccountResponse;
import com.Banking.system.enums.AccountStatus;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(String email, createAccountRequest request);
    List<AccountResponse> getMyAccounts(String email);
    AccountResponse getAccountById(Long id, String email);
    AccountResponse deactivateAccount(Long id, String email);


    List<AccountResponse> getAllAccounts();
    AccountResponse updateAccountStatus(Long id, AccountStatus status);

}
