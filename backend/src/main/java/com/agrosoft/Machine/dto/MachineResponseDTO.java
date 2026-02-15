package com.agrosoft.Machine.dto;

import com.agrosoft.Machine.domain.MachineStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MachineResponseDTO {

    private UUID id;
    private String name;
    private String type;
    private String brand;
    private String model;
    private Integer manufacturingYear;
    private String serialNumber;
    private MachineStatus status;
    private BigDecimal purchaseValue;
    private LocalDate purchaseDate;
    private UUID assignedEmployeeId;
    private String assignedEmployeeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
