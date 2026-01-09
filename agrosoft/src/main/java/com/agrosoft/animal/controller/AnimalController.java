package com.agrosoft.animal.controller;

import com.agrosoft.animal.dto.AnimalResponseDTO;
import com.agrosoft.animal.dto.CreateAnimalRequestDTO;
import com.agrosoft.animal.dto.UpdateAnimalRequestDTO;
import com.agrosoft.animal.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }


    @PostMapping
    public ResponseEntity<AnimalResponseDTO> create(@RequestBody CreateAnimalRequestDTO dto) {
        AnimalResponseDTO response = animalService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> findAll() {
        return ResponseEntity.ok(animalService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(animalService.findById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UpdateAnimalRequestDTO dto
    ) {
        return ResponseEntity.ok(animalService.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        animalService.deactivate(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AnimalResponseDTO>> findByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(animalService.findByEmployee(employeeId));
    }
}
