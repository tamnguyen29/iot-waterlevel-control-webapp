package com.nvt.iot.service;

import com.nvt.iot.model.ClientType;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.security.Principal;
import java.util.List;

public interface WaterTankConnectionService extends WebsocketHandleEventService {
    void sendListMemberToEveryUserClient(SimpMessageHeaderAccessor headerAccessor);

    void connectToDevice(String deviceId, Principal user);

    default void sendListUserToAllUser() {
    }

    default void sendListDeviceToAllUser() {
    }

    default List<?> getConnectedUsersOrDevicesList(ClientType type) {
        return null;
    }

    default void sendUserInfoToSpecificDevice(String user, String deviceName) {
    }

    void stopConnectToDevice(String deviceId, Principal user);
}
