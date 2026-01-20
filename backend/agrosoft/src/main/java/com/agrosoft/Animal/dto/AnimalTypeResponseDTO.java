package com.agrosoft.Animal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AnimalTypeResponseDTO {

    private UUID id;
    private String name;
}
