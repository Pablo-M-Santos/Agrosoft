package com.agrosoft.Employee.dto;

import com.agrosoft.Employee.domain.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateEmployeeRequestDTO {

    private String fullName;
    private String phone;
    private String address;
    private String photoUrl;
    private String relatedMachinery;
    private BigDecimal salary;
    private String contractType;
    private EmployeeStatus status;
    private LocalDate terminationDate;

}
