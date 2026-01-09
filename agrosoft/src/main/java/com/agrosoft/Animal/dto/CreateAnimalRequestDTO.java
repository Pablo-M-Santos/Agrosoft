package com.agrosoft.Animal.dto;

import com.agrosoft.Animal.domain.AnimalStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateAnimalRequestDTO {

    private String name;
    private String type;
    private BigDecimal weight;
    private LocalDate entryDate;
    private AnimalStatus status;
    private UUID responsibleEmployeeId;

}
