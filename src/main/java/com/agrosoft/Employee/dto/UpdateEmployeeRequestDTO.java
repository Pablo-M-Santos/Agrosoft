package com.agrosoft.Employee.dto;

import com.agrosoft.Employee.domain.ContractType;
import com.agrosoft.Employee.domain.DriverLicenseCategory;
import com.agrosoft.Employee.domain.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateEmployeeRequestDTO {

    @Size(max = 150, message = "Full name must have at most 150 characters")
    private String fullName;

    @Email(message = "Email must be valid")
    @Size(max = 100)
    private String email;

    @Pattern(
            regexp = "\\d{11}",
            message = "CPF must contain exactly 11 digits"
    )
    private String cpf;

    @Size(max = 20)
    private String rg;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Pattern(
            regexp = "\\d{10,15}",
            message = "Phone must contain 10 to 15 digits"
    )
    private String phone;

    private String address;

    @Size(max = 255)
    private String photoUrl;

    private DriverLicenseCategory driverLicenseCategory;

    @Size(max = 100)
    private String workArea;

    @Size(max = 100)
    private String relatedMachinery;

    private LocalDate hireDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero")
    private BigDecimal salary;

    private ContractType contractType;

    private EmployeeStatus status;
}