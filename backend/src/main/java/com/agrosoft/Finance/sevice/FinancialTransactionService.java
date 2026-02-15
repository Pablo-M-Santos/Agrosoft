package com.agrosoft.Finance.sevice;

import com.agrosoft.Finance.domain.Financial;
import com.agrosoft.Finance.domain.TransactionType;
import com.agrosoft.Finance.dto.CreateFinancialTransactionDTO;
import com.agrosoft.Finance.dto.FinancialStatsDTO;
import com.agrosoft.Finance.dto.FinancialTransactionResponseDTO;
import com.agrosoft.Finance.dto.UpdateFinancialTransactionDTO;
import com.agrosoft.Finance.repository.FinancialTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FinancialTransactionService {

    private final FinancialTransactionRepository repository;

    public FinancialTransactionService(FinancialTransactionRepository repository) {
        this.repository = repository;
    }

    public FinancialTransactionResponseDTO create(CreateFinancialTransactionDTO dto) {

        if (dto.getAmount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }


        Financial tx = new Financial();
        tx.setDescription(dto.getDescription());
        tx.setAmount(dto.getAmount());
        tx.setType(dto.getType());
        tx.setCategory(dto.getCategory());
        tx.setTransactionDate(dto.getTransactionDate());

        return toResponseDTO(repository.save(tx));
    }

    public List<FinancialTransactionResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public FinancialTransactionResponseDTO findById(UUID id) {
        Financial tx = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        return toResponseDTO(tx);
    }

    public FinancialTransactionResponseDTO update(UUID id, UpdateFinancialTransactionDTO dto) {
        Financial tx = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        if (dto.getDescription() != null) tx.setDescription(dto.getDescription());
        if (dto.getAmount() != null) tx.setAmount(dto.getAmount());
        if (dto.getType() != null) tx.setType(dto.getType());
        if (dto.getCategory() != null) tx.setCategory(dto.getCategory());
        if (dto.getTransactionDate() != null) tx.setTransactionDate(dto.getTransactionDate());


        Financial updated = repository.save(tx);
        return toResponseDTO(updated);
    }

    public FinancialStatsDTO getStats() {
        BigDecimal totalRevenues = repository.sumByType(TransactionType.REVENUE);
        BigDecimal totalExpenses = repository.sumByType(TransactionType.EXPENSE);

        totalRevenues = (totalRevenues != null) ? totalRevenues : BigDecimal.ZERO;
        totalExpenses = (totalExpenses != null) ? totalExpenses : BigDecimal.ZERO;


        BigDecimal totalMovimentado = totalRevenues.add(totalExpenses);


        BigDecimal saldoReal = totalRevenues.subtract(totalExpenses);

        FinancialStatsDTO stats = new FinancialStatsDTO();
        stats.setRevenues(totalRevenues);
        stats.setExpenses(totalExpenses);
        stats.setTotal(totalMovimentado);
        stats.setBalance(saldoReal);

        return stats;
    }


    public void delete(UUID id) {
        Financial tx = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        repository.delete(tx);
    }

    public List<FinancialTransactionResponseDTO> findByPeriod(LocalDate start, LocalDate end) {
        return repository.findByTransactionDateBetween(start, end)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private FinancialTransactionResponseDTO toResponseDTO(Financial tx) {
        FinancialTransactionResponseDTO dto = new FinancialTransactionResponseDTO();
        dto.setId(tx.getId());
        dto.setDescription(tx.getDescription());
        dto.setAmount(tx.getAmount());
        dto.setType(tx.getType());
        dto.setCategory(tx.getCategory());
        dto.setTransactionDate(tx.getTransactionDate());
        dto.setCreatedAt(tx.getCreatedAt());
        dto.setUpdatedAt(tx.getUpdatedAt());
        return dto;
    }
}