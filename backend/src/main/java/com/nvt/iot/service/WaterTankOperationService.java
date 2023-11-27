package com.nvt.iot.service;

import com.nvt.iot.model.DataFromDevice;
import com.nvt.iot.model.SignalControl;

public interface WaterTankOperationService {
    void startMeasurementWithControlParameter(String controllerId, String deviceId, String senderId);

    void stopMeasurement(String deviceId, String senderId);

    SignalControl getWaterLevelDataFromDevice(DataFromDevice data);

    double sendFirstData(DataFromDevice data);
}
