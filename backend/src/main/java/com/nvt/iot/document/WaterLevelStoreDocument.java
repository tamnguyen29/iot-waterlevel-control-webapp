package com.nvt.iot.document;

import com.nvt.iot.model.WaterLevelData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "WaterLevelStore")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelStoreDocument {
    @Id
    private String id;
    @Field(name = "userID")
    private String userId;
    @Field(name = "controllerID")
    private String controllerId;
    @Field(name = "deviceID")
    private String deviceId;
    @Field(name = "water_level")
    private List<WaterLevelData> waterLevelDataList;
}
