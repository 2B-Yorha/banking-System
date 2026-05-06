package com.Banking.system.dto.request;


import com.Banking.system.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class createAccountRequest {

    @NotNull
    private AccountType accountType;

}
