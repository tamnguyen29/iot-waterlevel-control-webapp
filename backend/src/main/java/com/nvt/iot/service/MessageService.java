package com.nvt.iot.service;

import com.nvt.iot.model.ClientType;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.security.Principal;
import java.util.List;

public interface MessageService extends WebsocketHandleEventService {
    void sendListMemberToEveryUserClient(SimpMessageHeaderAccessor headerAccessor);

    void connectToDevice(String deviceId, Principal user);

    default void sendListUserToAllUser() {
    }

    default void sendListDeviceToAllUser() {
    }

    default List<?> getConnectedUsersOrDevicesList(ClientType type) {
        return null;
    }

    default void sendUserIdToSpecificDevice(String userId, String deviceName) {
    }
}
