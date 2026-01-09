package com.agrosoft.finance.repository;

import com.agrosoft.finance.domain.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {


    List<FinancialTransaction> findByTransactionDateBetween(LocalDate start, LocalDate end);

}