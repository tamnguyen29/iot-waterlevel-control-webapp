package com.nvt.iot.document;

import com.nvt.iot.model.ControlParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "WaterLevelUpdate")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWaterLevelDocument {
    @Id
    private String id;
    @Field(name = "water_level")
    private double value;
    @Field(name = "timestamp")
    private Date time;
    @Field(name = "control_parameters")
    private ControlParameter controlParameter;
    @Field(name = "deviceID")
    private String deviceId;
}
