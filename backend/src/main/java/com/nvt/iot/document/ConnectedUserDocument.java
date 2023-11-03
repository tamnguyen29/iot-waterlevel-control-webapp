package com.nvt.iot.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "ConnectedClient_USER")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectedUserDocument {
    @Id
    private String id;
    @Field(name = "session_id")
    private String sessionId;
    @Field("name")
    private String name;
    @Field(name = "online_at")
    private Date onlineAt;
}
