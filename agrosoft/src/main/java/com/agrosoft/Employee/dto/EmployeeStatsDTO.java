package com.agrosoft.Employee.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeStatsDTO {
    private long total;
    private long active;
    private long inactive;
    private long onLeave;
}
