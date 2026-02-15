package com.agrosoft.Employee.repository;

import com.agrosoft.Employee.domain.Employee;
import com.agrosoft.Employee.domain.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

    long countByStatus(EmployeeStatus status);

    Optional<Employee> findByIdAndStatus(UUID id, EmployeeStatus status);

    List<Employee> findAllByStatus(EmployeeStatus status);

    Page<Employee> findAll(Pageable pageable);

    Page<Employee> findAllByStatus(EmployeeStatus status, Pageable pageable);
}