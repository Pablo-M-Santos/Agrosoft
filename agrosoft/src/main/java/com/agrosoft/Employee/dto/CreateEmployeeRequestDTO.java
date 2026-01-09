package com.agrosoft.Employee.dto;

import com.agrosoft.Employee.domain.EmployeeStatus;
import lombok.Getter;

import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateEmployeeRequestDTO {

    private String fullName;
    private String email;
    private String cpf;
    private String rg;
    private LocalDate birthDate;
    private String phone;
    private String address;
    private String photoUrl;
    private String driverLicenseCategory;
    private String workArea;
    private String relatedMachinery;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String contractType;
    private EmployeeStatus status;
}
