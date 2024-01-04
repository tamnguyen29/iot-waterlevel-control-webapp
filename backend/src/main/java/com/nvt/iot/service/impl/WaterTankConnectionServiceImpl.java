package com.nvt.iot.service.impl;

import com.nvt.iot.document.ConnectedDeviceDocument;
import com.nvt.iot.document.ConnectedUserDocument;
import com.nvt.iot.exception.DeviceNotAvailableException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.model.Action;
import com.nvt.iot.model.ClientType;
import com.nvt.iot.model.CurrentUsingUser;
import com.nvt.iot.model.UsingStatus;
import com.nvt.iot.payload.response.ConnectDeviceResponse;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import com.nvt.iot.service.WaterTankConnectionService;
import com.nvt.iot.service.WebsocketHandleEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterTankConnectionServiceImpl implements WaterTankConnectionService {
    private final ConnectedUserRepository connectedUserRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebsocketHandleEventService websocketHandleEventService;
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
                websocketHandleEventService.sendListUserToAllUser();
                websocketHandleEventService.sendListDeviceToSpecificUser(id);
            } else if (clientType.equals(ClientType.DEVICE)) {
                websocketHandleEventService.sendListDeviceToAllUser();
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

        } else {
            if (!connectedDevice.getCurrentUsingUser().getId().equals(userId)) {
                throw new DeviceNotAvailableException("Device already used by "
                    + connectedDevice.getCurrentUsingUser().getName());
            }
        }
        websocketHandleEventService.sendMessageToDevice(userId, deviceId, Action.USER_CONNECT_TO_DEVICE);
        websocketHandleEventService.sendListDeviceToAllUser();
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

            websocketHandleEventService.sendMessageToDevice(userId, deviceId, Action.USER_DISCONNECT_TO_DEVICE);
            websocketHandleEventService.sendListDeviceToAllUser();
        });
    }

    void validateIsNotNullOrEmpty(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationCustomException("Invalid id: " + id);
        }
    }
}
