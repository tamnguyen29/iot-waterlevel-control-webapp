package com.nvt.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlData {
    private String deviceId;
    private String deviceName;
    private String deviceDescription;
    private List<ControlUnit> controlUnitList;
}
