package com.Banking.system.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalAccounts;
    private long activeAccounts;
    private long totalTransactions;
    private long totalDeposits;
    private long totalWithdrawals;
    private long totalTransfers;
    private BigDecimal totalSystemBalance;

}
