package com.agrosoft.animal.service;

import com.agrosoft.Employee.domain.Employee;
import com.agrosoft.Employee.repository.EmployeeRepository;
import com.agrosoft.animal.domain.Animal;
import com.agrosoft.animal.domain.AnimalStatus;
import com.agrosoft.animal.dto.AnimalResponseDTO;
import com.agrosoft.animal.dto.CreateAnimalRequestDTO;
import com.agrosoft.animal.dto.UpdateAnimalRequestDTO;
import com.agrosoft.animal.repository.AnimalRepository;
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

    public AnimalService(AnimalRepository animalRepository,
                         EmployeeRepository employeeRepository) {
        this.animalRepository = animalRepository;
        this.employeeRepository = employeeRepository;
    }


    public AnimalResponseDTO create(CreateAnimalRequestDTO dto) {
        Animal animal = new Animal();
        animal.setName(dto.getName());
        animal.setType(dto.getType());
        animal.setWeight(dto.getWeight());
        animal.setEntryDate(dto.getEntryDate());
        animal.setStatus(dto.getStatus() != null ? dto.getStatus() : AnimalStatus.ACTIVE);

        if (dto.getResponsibleEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getResponsibleEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Employee not found"
                    ));
            animal.setResponsibleEmployee(employee);
        }

        Animal savedAnimal = animalRepository.save(animal);
        return toResponseDTO(savedAnimal);
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


    public AnimalResponseDTO update(UUID id, UpdateAnimalRequestDTO dto) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animal not found"
                ));

        animal.setName(dto.getName());
        animal.setType(dto.getType());
        animal.setWeight(dto.getWeight());
        if (dto.getStatus() != null) {
            animal.setStatus(dto.getStatus());
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

        Animal updatedAnimal = animalRepository.save(animal);
        return toResponseDTO(updatedAnimal);
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
        dto.setType(animal.getType());
        dto.setWeight(animal.getWeight());
        dto.setEntryDate(animal.getEntryDate());
        dto.setStatus(animal.getStatus());
        dto.setCreatedAt(animal.getCreatedAt());
        dto.setUpdatedAt(animal.getUpdatedAt());

        if (animal.getResponsibleEmployee() != null) {
            dto.setResponsibleEmployeeId(animal.getResponsibleEmployee().getId());
            dto.setResponsibleEmployeeName(animal.getResponsibleEmployee().getFullName());
        }

        return dto;
    }
}

