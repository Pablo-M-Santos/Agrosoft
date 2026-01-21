package com.agrosoft.Finance.dto;

import com.agrosoft.Finance.domain.FinancialCategory;
import com.agrosoft.Finance.domain.TransactionType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateFinancialTransactionDTO {

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotNull
    private FinancialCategory category;

    @NotNull
    private LocalDate transactionDate;

}
