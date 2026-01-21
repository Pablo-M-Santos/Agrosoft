package com.agrosoft.Employee.domain;


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
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(length = 20)
    private String rg;

    private LocalDate birthDate;

    @Column(length = 15)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private DriverLicenseCategory driverLicenseCategory;

    @Column(length = 100)
    private String workArea;

    @Column(length = 100)
    private String relatedMachinery;

    private LocalDate hireDate;

    private LocalDate terminationDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    public void deactivate() {
        if (this.status == EmployeeStatus.INACTIVE) {
            throw new IllegalStateException("Employee already inactive");
        }

        this.status = EmployeeStatus.INACTIVE;
        this.terminationDate = LocalDate.now();
    }

}
