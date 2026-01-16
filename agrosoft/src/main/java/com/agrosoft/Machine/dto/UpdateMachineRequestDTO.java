package com.agrosoft.Machine.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdateMachineRequestDTO {

    @Size(max = 100, message = "Name must have at most 100 characters")
    private String name;

    @Size(max = 50, message = "Type must have at most 50 characters")
    private String type;

    @Size(max = 50, message = "Brand must have at most 50 characters")
    private String brand;

    @Size(max = 50, message = "Model must have at most 50 characters")
    private String model;

    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase value must be greater than zero")
    private BigDecimal purchaseValue;

}
