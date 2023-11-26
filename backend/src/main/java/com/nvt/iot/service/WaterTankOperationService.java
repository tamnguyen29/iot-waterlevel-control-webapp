package com.nvt.iot.service;

import com.nvt.iot.model.DataFromDevice;

public interface WaterTankOperationService {
    void startMeasurementWithControlParameter(String controllerId, String deviceId, String senderId);

    void stopMeasurement(String deviceId, String senderId);

    double getWaterLevelDataFromDevice(DataFromDevice data);

    double sendFirstData(DataFromDevice data);
}
