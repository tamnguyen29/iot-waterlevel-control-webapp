package com.nvt.iot.service.impl;

import com.nvt.iot.document.ConnectedUserDocument;
import com.nvt.iot.model.Action;
import com.nvt.iot.model.ClientType;
import com.nvt.iot.model.Message;
import com.nvt.iot.model.Notification;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import com.nvt.iot.service.WebsocketHandleEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebsocketHandleEventServiceImpl implements WebsocketHandleEventService {
    @Value("${websocket.room.common}")
    private String CONNECTED_CLIENTS_ROOM;
    @Value(("${websocket.room.private-device}"))
    private String DEVICE_PRIVATE_ROOM;
    @Value(("${websocket.room.private-user}"))
    private String USER_PRIVATE_ROOM;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final ConnectedUserRepository connectedUserRepository;

    @Override
    public void sendListUserToAllUser() {
        var message = Message.builder()
            .action(Action.SEND_LIST_CONNECTED_USER)
            .content(getConnectedClientList(ClientType.USER))
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSend(CONNECTED_CLIENTS_ROOM, message);
    }

    @Override
    public void sendListDeviceToAllUser() {
        var message = Message.builder()
            .action(Action.SEND_LIST_CONNECTED_DEVICE)
            .content(getConnectedClientList(ClientType.DEVICE))
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSend(CONNECTED_CLIENTS_ROOM, message);
    }

    @Override
    public List<?> getConnectedClientList(ClientType type) {
        return (type.equals(ClientType.USER)) ?
            connectedUserRepository.findAll() : connectedDeviceRepository.findAll();
    }

    @Override
    public void sendMessageToDevice(String userId, String deviceName, Action action) {
        var message = Message.builder()
            .sender(userId)
            .action(action)
            .content(userId)
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSendToUser(deviceName, DEVICE_PRIVATE_ROOM, message);
    }

    @Override
    public void sendListDeviceToSpecificUser(String userId) {
        var message = Message.builder()
            .sender("SERVER")
            .action(Action.SEND_LIST_CONNECTED_DEVICE)
            .content(getConnectedClientList(ClientType.DEVICE))
            .time(new Date(System.currentTimeMillis()))
            .receiver(userId)
            .build();
        simpMessagingTemplate.convertAndSendToUser(userId,
            USER_PRIVATE_ROOM,
            message
        );
    }

    @Override
    public void sendNotificationExceptUser(String exceptUserId, Notification notification) {
        List<ConnectedUserDocument> onlineUserList = connectedUserRepository.findAll();
        onlineUserList.forEach((connectedUser -> {
            if (!connectedUser.getId().equals(exceptUserId)) {
                var message = Message.builder()
                    .sender("SERVER")
                    .action(Action.NOTIFICATION)
                    .time(new Date(System.currentTimeMillis()))
                    .content(notification)
                    .build();
                simpMessagingTemplate.convertAndSendToUser(
                    connectedUser.getId(),
                    USER_PRIVATE_ROOM,
                    message
                );
            }
        }));
    }
}
