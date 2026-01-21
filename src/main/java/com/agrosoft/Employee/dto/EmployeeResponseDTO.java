package com.agrosoft.Employee.dto;

import com.agrosoft.Employee.domain.ContractType;
import com.agrosoft.Employee.domain.DriverLicenseCategory;
import com.agrosoft.Employee.domain.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class EmployeeResponseDTO {

    private UUID id;
    private String fullName;
    private String email;
    private String cpf;
    private String rg;
    private LocalDate birthDate;
    private String phone;
    private String address;
    private String photoUrl;
    private DriverLicenseCategory driverLicenseCategory;
    private String workArea;
    private String relatedMachinery;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private BigDecimal salary;
    private ContractType contractType;
    private EmployeeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
