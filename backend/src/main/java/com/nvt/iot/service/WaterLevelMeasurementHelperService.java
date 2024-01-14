package com.nvt.iot.service;

import com.nvt.iot.model.ControlParameter;
import com.nvt.iot.model.WaterLevelData;

import java.util.Date;
import java.util.List;

public interface WaterLevelMeasurementHelperService {
    void createFirstWaterLevelUpdate(double value, Date time, String deviceId, ControlParameter controlParameter);

    void createFirstWaterLevelStore(String userId, String deviceId, String controllerId, List<WaterLevelData> list);

    void createFirstXControl(String deviceId);
}
