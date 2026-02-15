package com.agrosoft.Animal.service;

import com.agrosoft.Animal.domain.AnimalType;
import com.agrosoft.Animal.dto.AnimalStatsDTO;
import com.agrosoft.Employee.domain.Employee;
import com.agrosoft.Employee.repository.EmployeeRepository;
import com.agrosoft.Animal.domain.Animal;
import com.agrosoft.Animal.domain.AnimalStatus;
import com.agrosoft.Animal.dto.AnimalResponseDTO;
import com.agrosoft.Animal.dto.CreateAnimalRequestDTO;
import com.agrosoft.Animal.dto.UpdateAnimalRequestDTO;
import com.agrosoft.Animal.repository.AnimalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final EmployeeRepository employeeRepository;
    private final AnimalTypeService animalTypeService;

    public AnimalService(AnimalRepository animalRepository,
                         EmployeeRepository employeeRepository, AnimalTypeService animalTypeService) {
        this.animalRepository = animalRepository;
        this.employeeRepository = employeeRepository;
        this.animalTypeService = animalTypeService;
    }


    public AnimalResponseDTO create(CreateAnimalRequestDTO dto) {
        Animal animal = new Animal();
        animal.setName(dto.getName());
        animal.setWeight(dto.getWeight());
        animal.setEntryDate(dto.getEntryDate());
        animal.setStatus(dto.getStatus() != null ? dto.getStatus() : AnimalStatus.ACTIVE);

        AnimalType type;

        if (dto.getAnimalTypeId() != null) {
            type = animalTypeService.findById(dto.getAnimalTypeId());
        } else if (dto.getName() != null) {
            type = animalTypeService.findOrCreate(dto.getName());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Animal type is required"
            );
        }

        animal.setType(type);


        if (dto.getResponsibleEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getResponsibleEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Employee not found"
                    ));
            animal.setResponsibleEmployee(employee);
        }

        return toResponseDTO(animalRepository.save(animal));
    }


    public List<AnimalResponseDTO> findAll() {
        return animalRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    public AnimalResponseDTO findById(UUID id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animal not found"
                ));
        return toResponseDTO(animal);
    }

    public List<AnimalResponseDTO> findByType(UUID typeId) {
        return animalRepository.findByType_Id(typeId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    public AnimalStatsDTO getStats() {
        long total = animalRepository.count();
        long active = animalRepository.countByStatus(AnimalStatus.ACTIVE);
        long sold = animalRepository.countByStatus(AnimalStatus.SOLD);
        long underCare = animalRepository.countByStatus(AnimalStatus.UNDER_CARE);

        AnimalStatsDTO stats = new AnimalStatsDTO();
        stats.setTotal(total);
        stats.setActive(active);
        stats.setSold(sold);
        stats.setUnderCare(underCare);

        return stats;
    }



    public AnimalResponseDTO update(UUID id, UpdateAnimalRequestDTO dto) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animal not found"
                ));

        if (dto.getName() != null) animal.setName(dto.getName());
        if (dto.getWeight() != null) animal.setWeight(dto.getWeight());
        if (dto.getStatus() != null) animal.setStatus(dto.getStatus());

        if (dto.getAnimalTypeId() != null) {
            AnimalType type = animalTypeService.findById(dto.getAnimalTypeId());
            animal.setType(type);
        }


        if (dto.getResponsibleEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getResponsibleEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Employee not found"
                    ));
            animal.setResponsibleEmployee(employee);
        } else {
            animal.setResponsibleEmployee(null);
        }

        return toResponseDTO(animalRepository.save(animal));
    }


    public void deactivate(UUID id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animal not found"
                ));
        animal.setStatus(AnimalStatus.SOLD);
        animalRepository.save(animal);
    }


    public List<AnimalResponseDTO> findByEmployee(UUID employeeId) {
        return animalRepository.findByResponsibleEmployee_Id(employeeId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    private AnimalResponseDTO toResponseDTO(Animal animal) {
        AnimalResponseDTO dto = new AnimalResponseDTO();
        dto.setId(animal.getId());
        dto.setName(animal.getName());
        dto.setWeight(animal.getWeight());
        dto.setEntryDate(animal.getEntryDate());
        dto.setStatus(animal.getStatus());
        dto.setCreatedAt(animal.getCreatedAt());
        dto.setUpdatedAt(animal.getUpdatedAt());

        if (animal.getType() != null) {
            dto.setAnimalTypeId(animal.getType().getId());
            dto.setAnimalTypeName(animal.getType().getName());
        }

        if (animal.getResponsibleEmployee() != null) {
            dto.setResponsibleEmployeeId(animal.getResponsibleEmployee().getId());
            dto.setResponsibleEmployeeName(animal.getResponsibleEmployee().getFullName());
        }

        return dto;
    }
}

