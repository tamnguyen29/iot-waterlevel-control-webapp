package com.nvt.iot.service;

import com.nvt.iot.model.ClientType;

import java.util.List;

public interface WebsocketHandleEventService {
    void sendListUserToAllUser();

    void sendListDeviceToAllUser();

    List<?> getConnectedUsersOrDevicesList(ClientType type);

    void sendUserInfoToSpecificDevice(String user, String deviceName);
}
