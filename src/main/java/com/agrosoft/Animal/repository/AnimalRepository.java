package com.agrosoft.Animal.repository;

import com.agrosoft.Animal.domain.Animal;
import com.agrosoft.Animal.domain.AnimalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnimalRepository extends JpaRepository<Animal, UUID> {
    List<Animal> findByResponsibleEmployee_Id(UUID employeeId);

    List<Animal> findByType_Id(UUID typeId);

    long countByStatus(AnimalStatus status);
}
