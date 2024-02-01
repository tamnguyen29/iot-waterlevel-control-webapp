package com.nvt.iot.service;

import com.nvt.iot.model.ControlParameter;

import java.util.Date;

public interface WaterLevelMeasurementHelperService {
    void createFirstWaterLevelUpdate(double value, Date time, String deviceId, ControlParameter controlParameter);

    void createFirstWaterLevelStore(String userId, String deviceId, String controllerId);

    void createFirstXControl(String deviceId);
}
