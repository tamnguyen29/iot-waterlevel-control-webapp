package com.nvt.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaterLevelData {
    @Field(name = "value")
    private double value;
    @Field(name = "timestamp")
    private Date time;
}
