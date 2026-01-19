package com.agrosoft.Machine.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateMachineRequestDTO {

    @Size(max = 150, message = "Name must have at most 150 characters")
    private String name;

    @Size(max = 150, message = "Type must have at most 150 characters")
    private String type;

    @Size(max = 150, message = "Brand must have at most 150 characters")
    private String brand;

    @Size(max = 150, message = "Model must have at most 150 characters")
    private String model;

    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase value must be zero or greater")
    private BigDecimal purchaseValue;

    @Min(value = 1900, message = "Manufacturing year must be greater than 1900")
    @Max(value = 2100, message = "Manufacturing year must be less than 2100")
    private Integer manufacturingYear;

    @Size(max = 50, message = "Serial number must have at most 50 characters")
    private String serialNumber;

    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;
}
