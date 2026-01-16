package com.agrosoft.Machine.controller;


import com.agrosoft.Machine.domain.MachineStatus;
import com.agrosoft.Machine.dto.*;
import com.agrosoft.Machine.service.MachineService;
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
@RequestMapping("/machines")
@Tag(name = "Machines", description = "Operations related to machine management")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new machine",
            description = "Creates a new machine with its main information. A machine can optionally be assigned to an employee."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Machine created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or duplicated serial number",
                    content = @Content)
    })
    public ResponseEntity<MachineResponseDTO> create(@Valid @RequestBody CreateMachineRequestDTO dto) {
        MachineResponseDTO response = machineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Get machines",
            description = "Returns a list of machines. You can filter by status and choose whether inactive machines should be included."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of machines retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class)))
    })
    public ResponseEntity<List<MachineResponseDTO>> findAll(
            @RequestParam(defaultValue = "false") Boolean includeInactive,
            @RequestParam(required = false) MachineStatus status
    ) {
        return ResponseEntity.ok(
                machineService.findAll(status, includeInactive)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a machine by ID",
            description = "Returns a single machine by its unique ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machine retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Machine not found",
                    content = @Content)
    })
    public ResponseEntity<MachineResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(machineService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update machine data",
            description = "Updates the main machine data such as name, type, brand, model and purchase value. Does not change status or employee assignment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machine updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Machine not found",
                    content = @Content)
    })
    public ResponseEntity<MachineResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMachineRequestDTO dto
    ) {
        return ResponseEntity.ok(machineService.updateMachine(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update machine status",
            description = "Updates the machine status (e.g. OPERATIONAL, UNDER_MAINTENANCE). Inactive machines cannot have their status changed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machine status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status change or machine inactive",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Machine not found",
                    content = @Content)
    })
    public ResponseEntity<MachineResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMachineStatusDTO dto
    ) {
        return ResponseEntity.ok(machineService.updateStatus(id, dto));
    }

    @PatchMapping("/{id}/assign")
    @Operation(
            summary = "Assign an employee to a machine",
            description = "Assigns an active employee to a machine"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee assigned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Machine inactive or invalid operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Machine or employee not found",
                    content = @Content)
    })
    public ResponseEntity<MachineResponseDTO> assignEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody AssignEmployeeDTO dto
    ) {
        return ResponseEntity.ok(machineService.assignEmployee(id, dto.getEmployeeId()));
    }


    @PatchMapping("/{id}/unassign")
    @Operation(
            summary = "Unassign employee from a machine",
            description = "Removes the employee assignment from a machine, returning it to stock"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee unassigned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Machine not found",
                    content = @Content)
    })
    public ResponseEntity<MachineResponseDTO> unassignEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(machineService.unassignEmployee(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deactivate a machine",
            description = "Marks a machine as inactive. Inactive machines cannot be reassigned or reactivated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Machine deactivated successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Machine not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Machine is already inactive",
                    content = @Content)
    })
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        machineService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(
            summary = "Get machines by employee",
            description = "Returns all machines assigned to a specific employee"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Machines retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MachineResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content)
    })
    public ResponseEntity<List<MachineResponseDTO>> findByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(
                machineService.findByEmployee(employeeId)
        );
    }
}
