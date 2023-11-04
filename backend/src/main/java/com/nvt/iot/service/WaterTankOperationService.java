package com.nvt.iot.service;

public interface WaterTankOperationService {
    void startMeasurementWithControlParameter(String controllerId, String deviceId, String senderId);

    void stopMeasurement(String deviceId, String senderId);
}
