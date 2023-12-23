package com.nvt.iot.service.impl;

import com.nvt.iot.document.ConnectedDeviceDocument;
import com.nvt.iot.document.ConnectedUserDocument;
import com.nvt.iot.exception.DeviceNotAvailableException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.model.*;
import com.nvt.iot.payload.response.ConnectDeviceResponse;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import com.nvt.iot.service.WaterTankConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
    public ConnectDeviceResponse connectToDevice(String deviceId, String userId) {
        validateIsNotNullOrEmpty(deviceId);
        validateIsNotNullOrEmpty(userId);

        ConnectedDeviceDocument connectedDevice = connectedDeviceRepository.findById(deviceId)
            .orElseThrow(
                () -> new NotFoundCustomException("Not found device with id " + deviceId)
            );

        if (connectedDevice.getUsingStatus().equals(UsingStatus.AVAILABLE)) {
            ConnectedUserDocument userDocument = connectedUserRepository.findById(userId).orElseThrow(
                () -> new NotFoundCustomException("Can't not find user information!")
            );
            var currentUsingUser = CurrentUsingUser.builder()
                .id(userDocument.getId())
                .name(userDocument.getName())
                .sessionId(userDocument.getSessionId())
                .build();
            connectedDevice.setUsingStatus(UsingStatus.UNAVAILABLE);
            connectedDevice.setCurrentUsingUser(currentUsingUser);
            connectedDeviceRepository.save(connectedDevice);

            sendListDeviceToAllUser();
            sendUserInfoToSpecificDevice(userId, deviceId);
        } else {
            if (!connectedDevice.getCurrentUsingUser().getId().equals(userId)) {
                throw new DeviceNotAvailableException("Device already used by "
                    + connectedDevice.getCurrentUsingUser().getName());
            }
        }
        return ConnectDeviceResponse.builder()
            .device(connectedDevice)
            .connectDeviceTime(new Date(System.currentTimeMillis()))
            .build();
    }

    @Override
    public void stopConnectToDevice(String deviceId, String userId) {
        validateIsNotNullOrEmpty(deviceId);
        validateIsNotNullOrEmpty(userId);

        //Check device is currently used by this user
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, userId)) {
            throw new NotFoundCustomException("Not found device or user connect!");
        }

        Optional<ConnectedDeviceDocument> deviceDocumentOptional = connectedDeviceRepository.findById(deviceId);
        deviceDocumentOptional.ifPresent((deviceDocument) -> {
            deviceDocument.setUsingStatus(UsingStatus.AVAILABLE);
            deviceDocument.setCurrentUsingUser(null);
            connectedDeviceRepository.save(deviceDocument);

            sendDisconnectMessageToSpecificDevice(deviceId, userId);
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

    void validateIsNotNullOrEmpty(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationCustomException("Invalid id: " + id);
        }
    }
}
