package com.nvt.iot.document;

import com.nvt.iot.model.ControlUnit;
import com.nvt.iot.model.Creator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "WaterLevel")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWaterLevelDocument {
    @Id
    private String id;
    @Field(name = "value")
    private double value;
    @Field(name = "created_at")
    private Date createdAt;
    @Field(name = "creator")
    private Creator creator;
    @Field(name = "controller")
    private ControlUnit controlUnit;
}
