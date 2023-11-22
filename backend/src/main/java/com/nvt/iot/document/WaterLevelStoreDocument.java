package com.nvt.iot.document;

import com.nvt.iot.model.ControlUnit;
import com.nvt.iot.model.Creator;
import com.nvt.iot.model.WaterLevelStore;
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
    @Field("creator")
    private Creator creator;
    @Field("controller")
    private ControlUnit controlUnit;
    @Field("water_level")
    private List<WaterLevelStore> waterLevels;
}
