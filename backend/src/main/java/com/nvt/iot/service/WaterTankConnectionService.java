package com.nvt.iot.service;

import com.nvt.iot.model.ClientType;
import com.nvt.iot.payload.response.ConnectDeviceResponse;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;

public interface WaterTankConnectionService extends WebsocketHandleEventService {
    void sendListMemberToEveryUserClient(SimpMessageHeaderAccessor headerAccessor);

    ConnectDeviceResponse connectToDevice(String deviceId, String userId);

    default void sendListUserToAllUser() {
    }

    default void sendListDeviceToAllUser() {
    }

    default List<?> getConnectedUsersOrDevicesList(ClientType type) {
        return null;
    }

    default void sendUserInfoToSpecificDevice(String user, String deviceName) {
    }

    void stopConnectToDevice(String deviceId, String userId);
}
