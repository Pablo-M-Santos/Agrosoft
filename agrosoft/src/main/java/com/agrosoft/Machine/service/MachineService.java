package com.agrosoft.Machine.service;


import com.agrosoft.Employee.domain.Employee;
import com.agrosoft.Employee.repository.EmployeeRepository;
import com.agrosoft.Machine.domain.Machine;
import com.agrosoft.Machine.domain.MachineStatus;
import com.agrosoft.Machine.dto.CreateMachineRequestDTO;
import com.agrosoft.Machine.dto.MachineResponseDTO;
import com.agrosoft.Machine.dto.UpdateMachineRequestDTO;
import com.agrosoft.Machine.dto.UpdateMachineStatusDTO;
import com.agrosoft.Machine.repository.MachineRepository;
import com.agrosoft.exception.BusinessException;
import com.agrosoft.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MachineService {

    private final MachineRepository machineRepository;
    private final EmployeeRepository employeeRepository;

    public MachineService(
            MachineRepository machineRepository,
            EmployeeRepository employeeRepository
    ) {
        this.machineRepository = machineRepository;
        this.employeeRepository = employeeRepository;
    }

    public MachineResponseDTO create(CreateMachineRequestDTO dto) {

        if (dto.getSerialNumber() != null && machineRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new BusinessException("Machine with this serial number already exists");
        }

        Machine machine = buildMachine(dto);
        Machine savedMachine = machineRepository.save(machine);
        return toResponseDTO(savedMachine);
    }


    public List<MachineResponseDTO> findAll(MachineStatus status, Boolean includeInactive) {

        List<Machine> machines;

        if (status != null) {
            machines = machineRepository.findByStatus(status);
        } else if (Boolean.FALSE.equals(includeInactive)) {
            machines = machineRepository.findByStatusNot(MachineStatus.INACTIVE);
        } else {
            machines = machineRepository.findAll();
        }

        return machines.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }



    public MachineResponseDTO findById(UUID id) {
        Machine machine = findActiveMachine(id);
        return toResponseDTO(machine);
    }




    public MachineResponseDTO updateMachine(UUID id, UpdateMachineRequestDTO dto) {
        Machine machine = findActiveMachine(id);

        if (dto.getName() != null) machine.setName(dto.getName());
        if (dto.getType() != null) machine.setType(dto.getType());
        if (dto.getBrand() != null) machine.setBrand(dto.getBrand());
        if (dto.getModel() != null) machine.setModel(dto.getModel());
        if (dto.getPurchaseValue() != null) machine.setPurchaseValue(dto.getPurchaseValue());

        return toResponseDTO(machineRepository.save(machine));
    }

    public MachineResponseDTO updateStatus(UUID id, UpdateMachineStatusDTO dto) {
        Machine machine = findActiveMachine(id);

        if (dto.getStatus() == null) {
            throw new BusinessException("Status is required");
        }

        machine.setStatus(dto.getStatus());
        return toResponseDTO(machineRepository.save(machine));
    }


    public MachineResponseDTO assignEmployee(UUID id, UUID employeeId) {
        Machine machine = findActiveMachine(id);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        machine.setAssignedEmployee(employee);
        return toResponseDTO(machineRepository.save(machine));
    }

    public MachineResponseDTO unassignEmployee(UUID id) {
        Machine machine = findActiveMachine(id);
        machine.setAssignedEmployee(null);
        return toResponseDTO(machineRepository.save(machine));
    }


    public void deactivate(UUID id) {
        Machine machine = findActiveMachine(id);
        machine.deactivate();
        machineRepository.save(machine);
    }

    public List<MachineResponseDTO> findByEmployee(UUID employeeId) {
        return machineRepository.findByAssignedEmployee_Id(employeeId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private Machine findActiveMachine(UUID id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found"));

        if (machine.getStatus() == MachineStatus.INACTIVE) {
            throw new BusinessException("Machine is inactive");
        }

        return machine;
    }

    private Machine buildMachine(CreateMachineRequestDTO dto) {
        Machine machine = new Machine();

        machine.setName(dto.getName());
        machine.setType(dto.getType());
        machine.setBrand(dto.getBrand());
        machine.setModel(dto.getModel());
        machine.setSerialNumber(dto.getSerialNumber());
        machine.setPurchaseDate(dto.getPurchaseDate());
        machine.setPurchaseValue(dto.getPurchaseValue());
        machine.setStatus(MachineStatus.OPERATIONAL);

        if (dto.getAssignedEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
            machine.setAssignedEmployee(employee);
        }

        return machine;
    }

    private MachineResponseDTO toResponseDTO(Machine machine) {
        MachineResponseDTO dto = new MachineResponseDTO();

        dto.setId(machine.getId());
        dto.setName(machine.getName());
        dto.setType(machine.getType());
        dto.setBrand(machine.getBrand());
        dto.setModel(machine.getModel());
        dto.setManufacturingYear(machine.getManufacturingYear());
        dto.setSerialNumber(machine.getSerialNumber());
        dto.setStatus(machine.getStatus());
        dto.setPurchaseDate(machine.getPurchaseDate());
        dto.setPurchaseValue(machine.getPurchaseValue());

        if (machine.getAssignedEmployee() != null) {
            dto.setAssignedEmployeeId(machine.getAssignedEmployee().getId());
            dto.setAssignedEmployeeName(machine.getAssignedEmployee().getFullName());
        }

        dto.setCreatedAt(machine.getCreatedAt());
        dto.setUpdatedAt(machine.getUpdatedAt());

        return dto;
    }


}
