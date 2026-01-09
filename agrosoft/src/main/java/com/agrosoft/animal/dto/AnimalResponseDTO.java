package com.agrosoft.animal.dto;

import com.agrosoft.animal.domain.AnimalStatus;
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
    private String type;
    private BigDecimal weight;
    private LocalDate entryDate;
    private AnimalStatus status;
    private UUID responsibleEmployeeId;
    private String responsibleEmployeeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
