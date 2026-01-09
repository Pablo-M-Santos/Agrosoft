package com.agrosoft.animal.dto;

import com.agrosoft.animal.domain.AnimalStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateAnimalRequestDTO {

    private String name;
    private String type;
    private BigDecimal weight;
    private AnimalStatus status;
    private UUID responsibleEmployeeId;

}
