package com.Banking.system.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {


    @NotNull
    private Long fromAccountId;

    @NotBlank
    private String toAccountNumber;

    @NotNull
    @DecimalMin(value = "50", message = "Amount must be greater than 50")
    private BigDecimal amount;

    private String description;


}
