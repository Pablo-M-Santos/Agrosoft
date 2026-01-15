package com.agrosoft.Employee.dto;

import com.agrosoft.Employee.domain.ContractType;
import com.agrosoft.Employee.domain.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateEmployeeRequestDTO {

    @Size(max = 150)
    private String fullName;

    @Pattern(
            regexp = "\\d{10,15}",
            message = "Phone must contain 10 to 15 digits"
    )
    private String phone;

    private String address;

    @Size(max = 255)
    private String photoUrl;

    @Size(max = 100)
    private String relatedMachinery;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salary;

    private ContractType contractType;
}
