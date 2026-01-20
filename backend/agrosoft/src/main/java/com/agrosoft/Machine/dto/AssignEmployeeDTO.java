package com.agrosoft.Machine.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class AssignEmployeeDTO {

    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
}
