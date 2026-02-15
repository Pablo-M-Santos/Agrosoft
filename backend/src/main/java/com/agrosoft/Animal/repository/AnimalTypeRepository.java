package com.agrosoft.Animal.repository;

import com.agrosoft.Animal.domain.AnimalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnimalTypeRepository extends JpaRepository<AnimalType, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Optional<AnimalType> findByNameIgnoreCase(String name);
}
