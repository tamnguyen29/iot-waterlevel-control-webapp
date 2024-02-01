package com.nvt.iot.service;

import com.nvt.iot.payload.request.DeviceStatusRequest;
import com.nvt.iot.payload.response.ConnectDeviceResponse;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.validation.BindingResult;

public interface WaterTankConnectionService {
    void sendListMemberToEveryUserClient(SimpMessageHeaderAccessor headerAccessor);
    ConnectDeviceResponse connectToDevice(String deviceId, String userId);
    void stopConnectToDevice(String deviceId, String userId);
    void sendDeviceStatus(DeviceStatusRequest deviceStatusRequest, BindingResult bindingResult);
}
