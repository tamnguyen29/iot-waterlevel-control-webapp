package com.nvt.iot.document;

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
    @Field(name = "value")
    private double value;
    @Field(name = "created_at")
    private Date createdAt;
    @Field(name = "userID")
    private String userId;
    @Field(name = "controllerID")
    private String controlUnitId;
    @Field(name = "deviceID")
    private String deviceId;
}
