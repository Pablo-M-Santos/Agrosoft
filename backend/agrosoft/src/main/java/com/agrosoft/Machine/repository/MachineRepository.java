package com.agrosoft.Machine.repository;

import com.agrosoft.Machine.domain.Machine;
import com.agrosoft.Machine.domain.MachineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MachineRepository extends JpaRepository<Machine, UUID> {

    boolean existsBySerialNumber(String serialNumber);

    List<Machine> findByStatus(MachineStatus status);

    List<Machine> findByStatusNot(MachineStatus status);

    List<Machine> findByAssignedEmployee_Id(UUID employeeId);

    long countByStatus(MachineStatus status);


    List<Machine> findByNameContainingIgnoreCase(String name);

    Optional<Machine> findBySerialNumber(String serialNumber);

}

