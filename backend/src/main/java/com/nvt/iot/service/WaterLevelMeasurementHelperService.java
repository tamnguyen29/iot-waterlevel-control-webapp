package com.nvt.iot.service;

import com.nvt.iot.model.WaterLevelData;

import java.util.List;

public interface WaterLevelMeasurementHelperService {
    void createFirstWaterLevelUpdate(String userId, String deviceId, String controllerId);

    void createFirstXControl(String deviceId);

    void createFirstWaterLevelStore(String userId, String deviceId, String controllerId, List<WaterLevelData> list);
}
