package com.agrosoft.Machine.dto;

import com.agrosoft.Machine.domain.MachineStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateMachineStatusDTO {

    @NotNull(message = "Status is required")
    private MachineStatus status;
}
