package com.agrosoft.Employee.controller;

import com.agrosoft.Employee.dto.CreateEmployeeRequestDTO;
import com.agrosoft.Employee.dto.EmployeeResponseDTO;
import com.agrosoft.Employee.dto.UpdateEmployeeRequestDTO;
import com.agrosoft.Employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees", description = "Operations related to employee management")
public class EmployeeController {

    private final EmployeeService employeeService;


    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping
    @Operation(summary = "Create a new employee", description = "Creates a new employee with all required and optional fields")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<EmployeeResponseDTO> create(
            @Valid @RequestBody CreateEmployeeRequestDTO dto
    ) {
        EmployeeResponseDTO response = employeeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @Operation(summary = "Get all active employees", description = "Returns a list of all active employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employees retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class)))
    })
    public ResponseEntity<List<EmployeeResponseDTO>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by ID", description = "Returns a single employee by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    public ResponseEntity<EmployeeResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(employeeService.findById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update an existing employee", description = "Updates one or more fields of an existing employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    public ResponseEntity<EmployeeResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequestDTO dto
    ) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate an employee", description = "Marks an employee as inactive instead of deleting them")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deactivated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Employee is already inactive", content = @Content)
    })
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id
    ) {
        employeeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
