package com.agrosoft.Employee.controller;

import com.agrosoft.Employee.dto.CreateEmployeeRequestDTO;
import com.agrosoft.Employee.dto.EmployeeResponseDTO;
import com.agrosoft.Employee.dto.UpdateEmployeeRequestDTO;
import com.agrosoft.Employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;


    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(
            @RequestBody CreateEmployeeRequestDTO dto
    ) {
        EmployeeResponseDTO response = employeeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(employeeService.findById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UpdateEmployeeRequestDTO dto
    ) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id
    ) {
        employeeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
