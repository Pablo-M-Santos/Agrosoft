package com.agrosoft.Employee.controller;

import com.agrosoft.Employee.domain.EmployeeStatus;
import com.agrosoft.Employee.dto.CreateEmployeeRequestDTO;
import com.agrosoft.Employee.dto.EmployeeResponseDTO;
import com.agrosoft.Employee.dto.EmployeeStatsDTO;
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
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PostMapping("/batch")
    @Operation(summary = "Create multiple employees", description = "Creates multiple employees at once")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employees created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<List<EmployeeResponseDTO>> createBatch(
            @Valid @RequestBody List<CreateEmployeeRequestDTO> dtos
    ) {
        List<EmployeeResponseDTO> responses = dtos.stream()
                .map(employeeService::create)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }



    @GetMapping
    @Operation(summary = "Get all employees", description = "Returns a list of employees, optionally filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employees retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class)))
    })
    public ResponseEntity<List<EmployeeResponseDTO>> findAll(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(employeeService.findAll(status));
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

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Change employee status",
            description = "Updates the status of an existing employee. Possible statuses: ACTIVE, ON_LEAVE, INACTIVE, TERMINATED"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid status provided", content = @Content)
    })
    public ResponseEntity<EmployeeResponseDTO> changeStatus(
            @PathVariable UUID id,
            @RequestParam String status
    ) {
        EmployeeStatus newStatus;
        try {
            newStatus = EmployeeStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status provided");
        }

        EmployeeResponseDTO updated = employeeService.changeStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get employee stats", description = "Returns counts of employees by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee stats retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeStatsDTO.class)))
    })
    public ResponseEntity<EmployeeStatsDTO> getStats() {
        return ResponseEntity.ok(employeeService.getStats());
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
