package com.nvt.iot.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlUnitRequest {
    @NotNull(message = "kp is required!")
    @DecimalMin(value = "0.0", message = "kp must be greater than 0")
    private Double kp;

    @NotNull(message = "Name is required!")
    @NotEmpty(message = "Name can not be empty!")
    private String name;

    @NotNull(message = "Setpoint is required!")
    @DecimalMin(value = "0.0", message = "Setpoint must be greater than 0")
    @DecimalMax(value = "30.0", message = "Setpoint must be smaller than 30")
    private Double setpoint;

    @NotNull(message = "Creator is required!")
    @NotEmpty(message = "Creator id can not be empty!")
    private String userId;
}
