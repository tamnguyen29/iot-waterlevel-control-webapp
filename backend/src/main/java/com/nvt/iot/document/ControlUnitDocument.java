package com.nvt.iot.document;

import com.nvt.iot.model.Creator;
import com.nvt.iot.model.KalmanFilter;
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
    @Field(name = "kalmanfilter")
    private KalmanFilter kalmanFilter;
    @Field(name = "creator")
    private Creator creator;

    @Field(name = "created_at")
    private Date createdAt;

    @Field(name = "updated_at")
    private Date updatedAt;
}
