package com.agrosoft.Finance.dto;

import com.agrosoft.Finance.domain.FinancialCategory;
import com.agrosoft.Finance.domain.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateFinancialTransactionDTO {
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private FinancialCategory category;
    private LocalDate transactionDate;
}