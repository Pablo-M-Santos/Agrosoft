package com.agrosoft.Machine.service;


import com.agrosoft.Employee.domain.Employee;
import com.agrosoft.Employee.repository.EmployeeRepository;
import com.agrosoft.Machine.domain.Machine;
import com.agrosoft.Machine.domain.MachineStatus;
import com.agrosoft.Machine.dto.CreateMachineRequestDTO;
import com.agrosoft.Machine.dto.MachineResponseDTO;
import com.agrosoft.Machine.dto.UpdateMachineRequestDTO;
import com.agrosoft.Machine.repository.MachineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        if (machineRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Machine with this serial number already exists"
            );
        }

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
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Employee not found"
                    ));
            machine.setAssignedEmployee(employee);
        }

        Machine savedMachine = machineRepository.save(machine);
        return toResponseDTO(savedMachine);
    }


    public List<MachineResponseDTO> findAll() {
        return machineRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public MachineResponseDTO findById(UUID id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Machine not found"
                ));
        return toResponseDTO(machine);
    }


    public MachineResponseDTO update(UUID id, UpdateMachineRequestDTO dto) {

        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Machine not found"
                ));

        machine.setName(dto.getName());
        machine.setBrand(dto.getBrand());
        machine.setModel(dto.getModel());
        machine.setStatus(dto.getStatus());

        // Atualiza vínculo com funcionário
        if (dto.getAssignedEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getAssignedEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Employee not found"
                    ));
            machine.setAssignedEmployee(employee);
        } else {
            machine.setAssignedEmployee(null);
        }

        Machine updatedMachine = machineRepository.save(machine);
        return toResponseDTO(updatedMachine);
    }

    public void deactivate(UUID id) {

        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Machine not found"
                ));

        machine.setStatus(MachineStatus.INACTIVE);
        machineRepository.save(machine);
    }

    public List<MachineResponseDTO> findByEmployee(UUID employeeId) {
        return machineRepository.findByAssignedEmployee_Id(employeeId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    private MachineResponseDTO toResponseDTO(Machine machine) {

        MachineResponseDTO dto = new MachineResponseDTO();
        dto.setId(machine.getId());
        dto.setName(machine.getName());
        dto.setType(machine.getType());
        dto.setBrand(machine.getBrand());
        dto.setModel(machine.getModel());
        dto.setSerialNumber(machine.getSerialNumber());
        dto.setStatus(machine.getStatus());
        dto.setPurchaseDate(machine.getPurchaseDate());
        dto.setPurchaseValue(machine.getPurchaseValue());

        if (machine.getAssignedEmployee() != null) {
            dto.setAssignedEmployeeId(machine.getAssignedEmployee().getId());
            dto.setAssignedEmployeeName(machine.getAssignedEmployee().getFullName());
        }

        return dto;
    }


}
