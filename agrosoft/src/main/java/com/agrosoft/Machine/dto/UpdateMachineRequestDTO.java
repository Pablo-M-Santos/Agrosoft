package com.agrosoft.Machine.dto;

import com.agrosoft.Machine.domain.MachineStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateMachineRequestDTO {

    private String name;
    private String type;
    private String brand;
    private String model;
    private MachineStatus status;
    private BigDecimal purchaseValue;
    private UUID assignedEmployeeId;
}
