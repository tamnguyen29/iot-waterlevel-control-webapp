package com.nvt.iot.service.impl;

import com.nvt.iot.document.ConnectedDeviceDocument;
import com.nvt.iot.document.ConnectedUserDocument;
import com.nvt.iot.exception.WebsocketResourcesNotFoundException;
import com.nvt.iot.exception.WebsocketValidationException;
import com.nvt.iot.model.*;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import com.nvt.iot.service.WaterTankConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterTankConnectionServiceImpl implements WaterTankConnectionService {
    private final ConnectedUserRepository connectedUserRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Value("${websocket.request.handshake.parameter.client-id}")
    private String CLIENT_ID;
    @Value("${websocket.request.handshake.parameter.client-type}")
    private String CLIENT_TYPE;
    @Value("${websocket.room.common}")
    private String CONNECTED_CLIENTS_ROOM;
    @Value("${websocket.room.private-user}")
    private String USER_PRIVATE_ROOM;
    @Value("${websocket.room.private-device}")
    private String DEVICE_PRIVATE_ROOM;

    @Override
    public void sendListMemberToEveryUserClient(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();

        if (attributes != null) {
            String id = (String) attributes.get(CLIENT_ID);
            ClientType clientType = (ClientType) attributes.get(CLIENT_TYPE);

            if (clientType.equals(ClientType.USER)) {
                sendListUserToAllUser();
                sendListDeviceToSpecificUser(id);
            } else if (clientType.equals(ClientType.DEVICE)) {
                sendListDeviceToAllUser();
            }
        }
    }

    @Override
    public void connectToDevice(String deviceId, Principal user) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new WebsocketValidationException(
                String.format("Device id invalid when user(%s) try to connect.", user.getName()),
                user.getName());
        }
        ConnectedDeviceDocument connectedDevice = connectedDeviceRepository.findById(deviceId)
            .orElseThrow(() -> new WebsocketResourcesNotFoundException(
                String.format("Device not found with %s when user(%s) try to connect", deviceId, user.getName()),
                user.getName())
            );

        if (connectedDevice.getUsingStatus().equals(UsingStatus.AVAILABLE)) {
            ConnectedUserDocument userDocument = connectedUserRepository.findById(user.getName())
                .orElseThrow(() -> new WebsocketResourcesNotFoundException(
                    "Not found user " + user.getName(), user.getName())
                );
            var currentUsingUser = CurrentUsingUser.builder()
                .id(userDocument.getId())
                .name(userDocument.getName())
                .sessionId(userDocument.getSessionId())
                .build();
            connectedDevice.setUsingStatus(UsingStatus.UNAVAILABLE);
            connectedDevice.setCurrentUsingUser(currentUsingUser);
            connectedDeviceRepository.save(connectedDevice);

            sendUserInfoToSpecificDevice(user.getName(), deviceId);
            sendListDeviceToAllUser();
            //Send user connected successfully
            sendMessageToUserWhenTryingToConnectToDevice(user.getName(), ConnectDeviceStatus.SUCCESS);
        } else {
            sendMessageToUserWhenTryingToConnectToDevice(user.getName(), ConnectDeviceStatus.FAILURE);
        }


    }

    @Override
    public void stopConnectToDevice(String deviceId, Principal user) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new WebsocketValidationException(
                String.format("Device id invalid when user(%s) try to stop connecting.", user.getName()),
                user.getName());
        }

        //Check device is currently used by this user
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, user.getName())) {
            sendMessageToUserWhenTryingToDisConnectToDevice(user.getName(), DisconnectDeviceStatus.FAILURE);
            throw new WebsocketResourcesNotFoundException(
                String.format("Not found connected device[%s] or current using user[%s].", deviceId, user.getName()),
                user.getName()
            );
        }

        Optional<ConnectedDeviceDocument> deviceDocumentOptional = connectedDeviceRepository.findById(deviceId);
        deviceDocumentOptional.ifPresent((deviceDocument) -> {
            deviceDocument.setUsingStatus(UsingStatus.AVAILABLE);
            deviceDocument.setCurrentUsingUser(null);
            connectedDeviceRepository.save(deviceDocument);

            sendDisconnectMessageToSpecificDevice(deviceId, user.getName());
            sendMessageToUserWhenTryingToDisConnectToDevice(user.getName(), DisconnectDeviceStatus.SUCCESS);
            sendListDeviceToAllUser();
        });
    }

    @Override
    public void sendListUserToAllUser() {
        var message = Message.builder()
            .sender("SERVER")
            .action(Action.SEND_LIST_CONNECTED_USER)
            .content(getConnectedUsersOrDevicesList(ClientType.USER))
            .time(new Date(System.currentTimeMillis()))
            .receiver("ALL USERS")
            .build();
        simpMessagingTemplate.convertAndSend(CONNECTED_CLIENTS_ROOM, message);
    }

    @Override
    public void sendListDeviceToAllUser() {
        var message = Message.builder()
            .sender("SERVER")
            .action(Action.SEND_LIST_CONNECTED_DEVICE)
            .content(getConnectedUsersOrDevicesList(ClientType.DEVICE))
            .time(new Date(System.currentTimeMillis()))
            .receiver("ALL USERS")
            .build();
        simpMessagingTemplate.convertAndSend(CONNECTED_CLIENTS_ROOM, message);
    }

    @Override
    public List<?> getConnectedUsersOrDevicesList(ClientType type) {
        return type.equals(ClientType.USER) ?
            connectedUserRepository.findAll() : connectedDeviceRepository.findAll();
    }

    private void sendListDeviceToSpecificUser(String userName) {
        var messageToThisUser = Message.builder()
            .sender("SERVER")
            .action(Action.SEND_LIST_CONNECTED_DEVICE)
            .content(getConnectedUsersOrDevicesList(ClientType.DEVICE))
            .time(new Date(System.currentTimeMillis()))
            .receiver(userName)
            .build();
        simpMessagingTemplate.convertAndSendToUser(userName,
            USER_PRIVATE_ROOM,
            messageToThisUser
        );
    }

    private void sendMessageToUserWhenTryingToConnectToDevice(
        String userName,
        ConnectDeviceStatus connectDeviceStatus
    ) {
        var message = Message.builder()
            .action(Action.USER_CONNECT_TO_DEVICE)
            .sender("SERVER")
            .content(connectDeviceStatus)
            .time(new Date(System.currentTimeMillis()))
            .receiver(userName)
            .build();
        simpMessagingTemplate.convertAndSendToUser(userName, USER_PRIVATE_ROOM, message);
    }

    private void sendMessageToUserWhenTryingToDisConnectToDevice(
        String userName,
        DisconnectDeviceStatus disconnectDeviceStatus
    ) {
        var message = Message.builder()
            .sender("SERVER")
            .action(Action.USER_DISCONNECT_TO_DEVICE)
            .content(disconnectDeviceStatus)
            .time(new Date(System.currentTimeMillis()))
            .receiver(userName)
            .build();
        simpMessagingTemplate.convertAndSendToUser(userName, USER_PRIVATE_ROOM, message);
    }

    @Override
    public void sendUserInfoToSpecificDevice(String userId, String deviceName) {
        var message = Message.builder()
            .sender(userId)
            .action(Action.USER_CONNECT_TO_DEVICE)
            .content(userId)
            .time(new Date(System.currentTimeMillis()))
            .receiver(deviceName)
            .build();
        simpMessagingTemplate.convertAndSendToUser(deviceName, DEVICE_PRIVATE_ROOM, message);
    }

    private void sendDisconnectMessageToSpecificDevice(String deviceId, String userName) {
        var message = Message.builder()
            .sender(String.format("USER[%s]", userName))
            .action(Action.USER_DISCONNECT_TO_DEVICE)
            .content(userName)
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSendToUser(deviceId, DEVICE_PRIVATE_ROOM, message);
    }
}
