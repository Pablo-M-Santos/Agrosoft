package com.agrosoft.Animal.dto;

import com.agrosoft.Animal.domain.AnimalStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AnimalResponseDTO {

    private UUID id;
    private String name;
    private UUID animalTypeId;
    private String animalTypeName;
    private BigDecimal weight;
    private LocalDate entryDate;
    private AnimalStatus status;
    private UUID responsibleEmployeeId;
    private String responsibleEmployeeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
