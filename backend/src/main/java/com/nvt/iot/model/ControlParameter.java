package com.nvt.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlParameter {
    @Field(name = "kp")
    private double kp;
    @Field(name = "sp")
    private double setpoint;
}
