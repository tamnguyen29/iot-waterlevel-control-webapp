package com.nvt.iot.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "Controllers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlUnitDocument {
    @Id
    private String id;
    @Field(name = "kp")
    private double kp;
    @Field(name = "name")
    private String name;
    @Field(name = "setpoint")
    private double setpoint;
    @Field(name = "userID")
    private String userId;
    @Field(name = "created_at")
    private Date createdAt;

    @Field(name = "updated_at")
    private Date updatedAt;
}
