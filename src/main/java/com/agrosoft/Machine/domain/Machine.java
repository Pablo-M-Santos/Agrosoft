package com.agrosoft.Machine.domain;


import com.agrosoft.Employee.domain.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "machines")

public class Machine {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    private String brand;

    private String model;

    private Integer manufacturingYear;

    @Column(unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    private BigDecimal purchaseValue;

    private LocalDate purchaseDate;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee assignedEmployee;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void deactivate() {
        this.status = MachineStatus.INACTIVE;
    }

}
