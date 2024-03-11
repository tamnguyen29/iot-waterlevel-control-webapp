package com.nvt.iot.service;

import com.nvt.iot.model.Action;
import com.nvt.iot.model.ClientType;
import com.nvt.iot.model.Message;
import com.nvt.iot.model.Notification;

import java.util.List;

public interface WebsocketHandleEventService {
    void sendListUserToAllUser();

    void sendListDeviceToAllUser();

    List<?> getConnectedClientList(ClientType type);

    void sendMessageToDevice(String userId, String deviceName, Action action);

    void sendListDeviceToSpecificUser(String userId);

    void sendNotificationExceptUser(String exceptUserId, Notification notification);
    void sendNotificationAllUser(Notification notification);
    void sendMessageToSpecificUser(String userId, Message message);
}
