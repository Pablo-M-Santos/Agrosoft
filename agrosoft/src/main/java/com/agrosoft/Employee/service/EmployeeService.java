package com.agrosoft.Employee.service;


import com.agrosoft.Employee.domain.Employee;
import com.agrosoft.Employee.domain.EmployeeStatus;
import com.agrosoft.Employee.dto.CreateEmployeeRequestDTO;
import com.agrosoft.Employee.dto.EmployeeResponseDTO;
import com.agrosoft.Employee.dto.UpdateEmployeeRequestDTO;
import com.agrosoft.Employee.repository.EmployeeRepository;
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


        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already exists"
            );
        }

        if (employeeRepository.existsByCpf(dto.getCpf())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "CPF already exists"
            );
        }


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


        Employee savedEmployee = employeeRepository.save(employee);

        return toResponseDTO(savedEmployee);
    }


    public List<EmployeeResponseDTO> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    public EmployeeResponseDTO findById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found"
                ));

        return toResponseDTO(employee);
    }


    public EmployeeResponseDTO update(UUID id, UpdateEmployeeRequestDTO dto) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found"
                ));


        employee.setFullName(dto.getFullName());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());
        employee.setPhotoUrl(dto.getPhotoUrl());
        employee.setRelatedMachinery(dto.getRelatedMachinery());
        employee.setSalary(dto.getSalary());
        employee.setContractType(dto.getContractType());

        Employee updatedEmployee = employeeRepository.save(employee);

        return toResponseDTO(updatedEmployee);
    }


    public void deactivate(UUID id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found"
                ));

        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Employee already inactive"
            );
        }

        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
    }


    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setEmail(employee.getEmail());
        dto.setCpf(employee.getCpf());
        dto.setStatus(employee.getStatus());
        dto.setHireDate(employee.getHireDate());
        return dto;
    }
}
