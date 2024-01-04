package com.nvt.iot.service;

import com.nvt.iot.payload.response.ConnectDeviceResponse;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface WaterTankConnectionService {
    void sendListMemberToEveryUserClient(SimpMessageHeaderAccessor headerAccessor);

    ConnectDeviceResponse connectToDevice(String deviceId, String userId);

    void stopConnectToDevice(String deviceId, String userId);
}
