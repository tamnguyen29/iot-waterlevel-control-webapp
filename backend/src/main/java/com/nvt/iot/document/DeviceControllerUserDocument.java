package com.nvt.iot.document;

import com.nvt.iot.model.ControlData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "DeviceControllerUser")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceControllerUserDocument {
    @Id
    private String id;
    @Field(name = "userID")
    private String userId;
    @Field(name = "control-data")
    private List<ControlData> controlDataList;
}
