package com.nvt.iot.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "X-Control")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XControlDocument {
    @Id
    private String id;
    @Field("x_control")
    private double value;
}
