package com.Banking.system.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {

    @NotNull
    private Long accountId;

    @NotNull
    @DecimalMin(value = "50", message = "Amount must be greater than 50")
    private BigDecimal amount;

    private String description;


}
