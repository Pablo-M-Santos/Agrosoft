package com.agrosoft.Animal.dto;

import com.agrosoft.Animal.domain.AnimalStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateAnimalRequestDTO {

    @NotBlank(message = "Nome do animal é obrigatório")
    @Size(max = 100, message = "Nome do animal deve ter no máximo 100 caracteres")
    private String name;

    @NotNull(message = "Tipo do animal é obrigatório")
    private UUID animalTypeId;

    @NotNull(message = "Peso é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Peso deve ser maior que zero")
    private BigDecimal weight;

    @NotNull(message = "Data de entrada é obrigatória")
    @PastOrPresent(message = "Data de entrada não pode ser futura")
    private LocalDate entryDate;

    private AnimalStatus status;

    private UUID responsibleEmployeeId;

}
