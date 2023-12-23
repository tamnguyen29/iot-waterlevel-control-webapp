package com.nvt.iot.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

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
    @Field(name = "control-unit_device_ids")
    private Map<String, List<String>> deviceIdControlUnitIds;
}
