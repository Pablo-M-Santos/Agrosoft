package com.agrosoft.Finance.repository;

import com.agrosoft.Finance.domain.Financial;
import com.agrosoft.Finance.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialTransactionRepository extends JpaRepository<Financial, UUID> {

    long countByType(TransactionType type);

    @Query(
            "SELECT COALESCE(SUM(f.amount), 0) " +
                    "FROM Financial f " +
                    "WHERE f.type = :type"
    )
    BigDecimal sumByType(TransactionType type);

    List<Financial> findByTransactionDateBetween(LocalDate start, LocalDate end);

}