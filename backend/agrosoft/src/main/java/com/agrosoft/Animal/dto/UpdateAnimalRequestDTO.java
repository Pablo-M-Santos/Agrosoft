package com.agrosoft.Animal.dto;

import com.agrosoft.Animal.domain.AnimalStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateAnimalRequestDTO {

    private String name;
    private UUID animalTypeId;
    private BigDecimal weight;
    private AnimalStatus status;
    private UUID responsibleEmployeeId;

}
