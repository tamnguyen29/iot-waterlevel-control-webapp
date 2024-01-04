package com.nvt.iot.service;

import com.nvt.iot.model.DataFromDevice;
import com.nvt.iot.model.SignalControl;
import com.nvt.iot.payload.request.PumpOutRequest;
import org.springframework.validation.BindingResult;

public interface WaterTankOperationService {
    void startMeasurement(String controllerId, String deviceId, String userId);

    void stopMeasurement(String deviceId, String userId);

    SignalControl getWaterLevelDataFromDevice(DataFromDevice data);

    double sendFirstData(DataFromDevice data);

    void sendPumpOutSignal(PumpOutRequest pumpOutRequest, BindingResult error);
}
