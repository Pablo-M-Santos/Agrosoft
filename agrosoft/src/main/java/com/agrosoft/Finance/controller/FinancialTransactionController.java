package com.agrosoft.Finance.controller;

import com.agrosoft.Finance.dto.CreateFinancialTransactionDTO;
import com.agrosoft.Finance.dto.FinancialTransactionResponseDTO;
import com.agrosoft.Finance.dto.UpdateFinancialTransactionDTO;
import com.agrosoft.Finance.sevice.FinancialTransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/financial-transactions")
public class FinancialTransactionController {

    private final FinancialTransactionService service;

    public FinancialTransactionController(FinancialTransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FinancialTransactionResponseDTO> create(@RequestBody CreateFinancialTransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<FinancialTransactionResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialTransactionResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialTransactionResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UpdateFinancialTransactionDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/period")
    public ResponseEntity<List<FinancialTransactionResponseDTO>> findByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(service.findByPeriod(start, end));
    }
}