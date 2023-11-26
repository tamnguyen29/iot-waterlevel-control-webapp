package com.nvt.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataFromDevice {
    private double value;
    private String controlUnitId;
    private String userId;
    private String deviceId;
}
