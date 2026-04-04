package com.agrosoft.Animal.dto;

import com.agrosoft.Animal.domain.AnimalStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateAnimalRequestDTO {

    @Size(max = 100, message = "Nome do animal deve ter no máximo 100 caracteres")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Nome do animal não pode estar em branco")
    private String name;

    private UUID animalTypeId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Peso deve ser maior que zero")
    private BigDecimal weight;

    private AnimalStatus status;

    private UUID responsibleEmployeeId;

}
