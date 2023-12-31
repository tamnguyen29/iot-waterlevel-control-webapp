package com.nvt.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlUnit {
    private String id;
    private String name;
    private double kp;
    private double setpoint;
}
