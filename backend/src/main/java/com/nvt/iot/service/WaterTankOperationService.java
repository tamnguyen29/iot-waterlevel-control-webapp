package com.nvt.iot.service;

import com.nvt.iot.model.WaterLevelData;

public interface WaterTankOperationService {
    void startMeasurementWithControlParameter(String controllerId, String deviceId, String senderId);

    void stopMeasurement(String deviceId, String senderId);

    void getWaterLevelDataFromDevice(String fromDevice, WaterLevelData data);

    void sendSignalControlToDevice(String toDevice, double signalControl);
}
