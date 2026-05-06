package com.Banking.system.dto.response;


import com.Banking.system.enums.TransactionStatus;
import com.Banking.system.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    private Long id;
    private String referenceNumber;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private TransactionStatus status;
    private Long accountId;
    private String accountNumber;
    private Long relatedAccountId;
    private String relatedAccountNumber;
    private LocalDateTime createdAt;


}
