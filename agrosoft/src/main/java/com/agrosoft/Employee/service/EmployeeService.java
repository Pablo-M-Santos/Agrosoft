package com.agrosoft.Employee.service;


import com.agrosoft.Employee.domain.Employee;
import com.agrosoft.Employee.domain.EmployeeStatus;
import com.agrosoft.Employee.dto.CreateEmployeeRequestDTO;
import com.agrosoft.Employee.dto.EmployeeResponseDTO;
import com.agrosoft.Employee.dto.UpdateEmployeeRequestDTO;
import com.agrosoft.Employee.repository.EmployeeRepository;
import com.agrosoft.exception.BusinessException;
import com.agrosoft.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;


    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public EmployeeResponseDTO create(CreateEmployeeRequestDTO dto) {

        Employee employee = buildEmployee(dto);

        Employee savedEmployee = employeeRepository.save(employee);

        return toResponseDTO(savedEmployee);
    }

    public List<EmployeeResponseDTO> findAll() {
        return employeeRepository.findAllByStatus(EmployeeStatus.ACTIVE)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    public EmployeeResponseDTO findById(UUID id) {
        Employee employee = findActiveEmployee(id);
        return toResponseDTO(employee);
    }


    public EmployeeResponseDTO update(UUID id, UpdateEmployeeRequestDTO dto) {
        Employee employee = findActiveEmployee(id);

        if (dto.getFullName() != null) {
            employee.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            employee.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            employee.setAddress(dto.getAddress());
        }
        if (dto.getPhotoUrl() != null) {
            employee.setPhotoUrl(dto.getPhotoUrl());
        }
        if (dto.getRelatedMachinery() != null) {
            employee.setRelatedMachinery(dto.getRelatedMachinery());
        }
        if (dto.getSalary() != null) {
            employee.setSalary(dto.getSalary());
        }
        if (dto.getContractType() != null) {
            employee.setContractType(dto.getContractType());
        }

        return toResponseDTO(employeeRepository.save(employee));
    }



    public void deactivate(UUID id) {
        Employee employee = findActiveEmployee(id);
        employee.deactivate();
        employeeRepository.save(employee);
    }

    private Employee findActiveEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found")
                );

        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new BusinessException("Employee is inactive");
        }

        return employee;
    }

    private Employee buildEmployee(CreateEmployeeRequestDTO dto) {
        Employee employee = new Employee();

        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setCpf(dto.getCpf());
        employee.setRg(dto.getRg());
        employee.setBirthDate(dto.getBirthDate());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());
        employee.setPhotoUrl(dto.getPhotoUrl());
        employee.setDriverLicenseCategory(dto.getDriverLicenseCategory());
        employee.setWorkArea(dto.getWorkArea());
        employee.setRelatedMachinery(dto.getRelatedMachinery());
        employee.setHireDate(dto.getHireDate());
        employee.setSalary(dto.getSalary());
        employee.setContractType(dto.getContractType());
        employee.setStatus(EmployeeStatus.ACTIVE);

        return employee;
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();

        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setEmail(employee.getEmail());
        dto.setBirthDate(employee.getBirthDate());
        dto.setPhone(employee.getPhone());
        dto.setAddress(employee.getAddress());
        dto.setPhotoUrl(employee.getPhotoUrl());
        dto.setDriverLicenseCategory(employee.getDriverLicenseCategory());
        dto.setWorkArea(employee.getWorkArea());
        dto.setRelatedMachinery(employee.getRelatedMachinery());
        dto.setHireDate(employee.getHireDate());
        dto.setTerminationDate(employee.getTerminationDate());
        dto.setSalary(employee.getSalary());
        dto.setContractType(employee.getContractType());
        dto.setStatus(employee.getStatus());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());

        return dto;
    }

}
