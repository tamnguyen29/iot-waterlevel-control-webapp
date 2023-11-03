package com.nvt.iot.document;

import com.nvt.iot.model.CurrentUsingUser;
import com.nvt.iot.model.UsingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "ConnectedClient_DEVICE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectedDeviceDocument {
    @Id
    private String id;
    @Field(name = "device_name")
    private String name;
    @Field(name = "session_id")
    private String sessionId;
    @Field(name = "using_status")
    private UsingStatus usingStatus;
    @Field(name = "connected_user")
    private CurrentUsingUser currentUsingUser;
    @Field(name = "connected_at")
    private Date connectedAt;
}
