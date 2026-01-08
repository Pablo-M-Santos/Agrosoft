package com.agrosoft.Machine.controller;


import com.agrosoft.Machine.dto.CreateMachineRequestDTO;
import com.agrosoft.Machine.dto.MachineResponseDTO;
import com.agrosoft.Machine.dto.UpdateMachineRequestDTO;
import com.agrosoft.Machine.service.MachineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/machines")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping
    public ResponseEntity<MachineResponseDTO> create(@RequestBody CreateMachineRequestDTO dto) {
        MachineResponseDTO response = machineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MachineResponseDTO>> findAll() {
        return ResponseEntity.ok(machineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(machineService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MachineResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UpdateMachineRequestDTO dto
    ) {
        return ResponseEntity.ok(machineService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        machineService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<MachineResponseDTO>> findByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(
                machineService.findByEmployee(employeeId)
        );
    }
}
