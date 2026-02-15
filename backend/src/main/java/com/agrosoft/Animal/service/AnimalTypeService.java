package com.agrosoft.Animal.service;

import com.agrosoft.Animal.domain.AnimalType;
import com.agrosoft.Animal.dto.AnimalTypeResponseDTO;
import com.agrosoft.Animal.dto.CreateAnimalTypeRequestDTO;
import com.agrosoft.Animal.repository.AnimalTypeRepository;
import com.agrosoft.exception.BusinessException;
import com.agrosoft.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnimalTypeService {

    private final AnimalTypeRepository animalTypeRepository;

    public AnimalTypeService(AnimalTypeRepository animalTypeRepository) {
        this.animalTypeRepository = animalTypeRepository;
    }


    public AnimalType findById(UUID id) {
        return animalTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Animal type not found"));
    }


    public AnimalType findByName(String name) {
        return animalTypeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Animal type not found"));
    }


    public AnimalType findOrCreate(String name) {
        return animalTypeRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    AnimalType type = new AnimalType();
                    type.setName(name.trim());
                    return animalTypeRepository.save(type);
                });
    }


    public AnimalTypeResponseDTO create(CreateAnimalTypeRequestDTO dto) {
        if (animalTypeRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BusinessException("Animal type already exists");
        }

        AnimalType type = new AnimalType();
        type.setName(dto.getName().trim());

        return toResponseDTO(animalTypeRepository.save(type));
    }


    public List<AnimalTypeResponseDTO> findAll() {
        return animalTypeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    private AnimalTypeResponseDTO toResponseDTO(AnimalType type) {
        AnimalTypeResponseDTO dto = new AnimalTypeResponseDTO();
        dto.setId(type.getId());
        dto.setName(type.getName());
        return dto;
    }
}
