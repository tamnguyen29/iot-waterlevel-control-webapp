package com.nvt.iot.service;

import com.nvt.iot.model.DataFromDevice;
import com.nvt.iot.model.SignalControl;

public interface WaterTankOperationService {
    void startMeasurement(String controllerId, String deviceId, String userId);

    void stopMeasurement(String deviceId, String userId);

    SignalControl getWaterLevelDataFromDevice(DataFromDevice data);

    double sendFirstData(DataFromDevice data);
}
