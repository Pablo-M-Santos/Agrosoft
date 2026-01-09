package com.agrosoft.Finance.sevice;

import com.agrosoft.Finance.domain.FinancialTransaction;
import com.agrosoft.Finance.dto.CreateFinancialTransactionDTO;
import com.agrosoft.Finance.dto.FinancialTransactionResponseDTO;
import com.agrosoft.Finance.dto.UpdateFinancialTransactionDTO;
import com.agrosoft.Finance.repository.FinancialTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        FinancialTransaction tx = new FinancialTransaction();
        tx.setDescription(dto.getDescription());
        tx.setAmount(dto.getAmount());
        tx.setType(dto.getType());
        tx.setCategory(dto.getCategory());
        tx.setTransactionDate(dto.getTransactionDate());

        FinancialTransaction saved = repository.save(tx);
        return toResponseDTO(saved);
    }

    public List<FinancialTransactionResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public FinancialTransactionResponseDTO findById(UUID id) {
        FinancialTransaction tx = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        return toResponseDTO(tx);
    }

    public FinancialTransactionResponseDTO update(UUID id, UpdateFinancialTransactionDTO dto) {
        FinancialTransaction tx = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        tx.setDescription(dto.getDescription());
        tx.setAmount(dto.getAmount());
        tx.setType(dto.getType());
        tx.setCategory(dto.getCategory());
        tx.setTransactionDate(dto.getTransactionDate());

        FinancialTransaction updated = repository.save(tx);
        return toResponseDTO(updated);
    }

    public void delete(UUID id) {
        FinancialTransaction tx = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        repository.delete(tx);
    }

    public List<FinancialTransactionResponseDTO> findByPeriod(LocalDate start, LocalDate end) {
        return repository.findByTransactionDateBetween(start, end)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private FinancialTransactionResponseDTO toResponseDTO(FinancialTransaction tx) {
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