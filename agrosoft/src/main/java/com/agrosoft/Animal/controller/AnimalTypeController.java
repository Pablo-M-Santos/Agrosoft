package com.agrosoft.Animal.controller;

import com.agrosoft.Animal.dto.AnimalTypeResponseDTO;
import com.agrosoft.Animal.dto.CreateAnimalTypeRequestDTO;
import com.agrosoft.Animal.service.AnimalTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/animal-types")
public class AnimalTypeController {

    private final AnimalTypeService animalTypeService;

    public AnimalTypeController(AnimalTypeService animalTypeService) {
        this.animalTypeService = animalTypeService;
    }

    @PostMapping
    public ResponseEntity<AnimalTypeResponseDTO> create(
            @Valid @RequestBody CreateAnimalTypeRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(animalTypeService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AnimalTypeResponseDTO>> findAll() {
        return ResponseEntity.ok(animalTypeService.findAll());
    }
}
