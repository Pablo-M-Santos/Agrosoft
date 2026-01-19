package com.agrosoft.Machine.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MachineStatsDTO {
    private long total;
    private long operational;
    private long underMaintenance;
    private long inactive;
}
