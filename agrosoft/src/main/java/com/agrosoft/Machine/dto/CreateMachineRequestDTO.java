package com.agrosoft.Machine.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateMachineRequestDTO {

    private String name;
    private String type;
    private String brand;
    private String model;
    private Integer manufacturingYear;
    private String serialNumber;
    private BigDecimal purchaseValue;
    private LocalDate purchaseDate;
    private UUID assignedEmployeeId;

}
