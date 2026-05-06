package com.Banking.system.dto.response;


import com.Banking.system.enums.AccountStatus;
import com.Banking.system.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
    private Long userId;
    private String ownerFullName;
    private LocalDateTime createdAt;

}
