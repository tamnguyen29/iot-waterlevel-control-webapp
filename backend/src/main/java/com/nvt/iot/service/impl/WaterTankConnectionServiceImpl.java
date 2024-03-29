package com.nvt.iot.service.impl;

import com.nvt.iot.document.ConnectedDeviceDocument;
import com.nvt.iot.document.ConnectedUserDocument;
import com.nvt.iot.exception.DeviceNotAvailableException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.model.*;
import com.nvt.iot.payload.request.DeviceStatusRequest;
import com.nvt.iot.payload.response.ConnectDeviceResponse;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import com.nvt.iot.service.WaterLevelMeasurementHelperService;
import com.nvt.iot.service.WaterTankConnectionService;
import com.nvt.iot.service.WebsocketHandleEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterTankConnectionServiceImpl implements WaterTankConnectionService {
    private final ConnectedUserRepository connectedUserRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final WebsocketHandleEventService websocketHandleEventService;
    private final WaterLevelMeasurementHelperService waterLevelMeasurementHelperService;
    @Value("${websocket.request.handshake.parameter.client-id}")
    private String CLIENT_ID;
    @Value("${websocket.request.handshake.parameter.client-type}")
    private String CLIENT_TYPE;

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
                waterLevelMeasurementHelperService.createFirstWaterLevelUpdate(
                    0,
                    new Date(System.currentTimeMillis()),
                    id,
                    null
                );
                waterLevelMeasurementHelperService.createFirstXControl(id);
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

        ConnectedUserDocument userDocument = connectedUserRepository.findById(userId)
            .orElseThrow(
                () -> new NotFoundCustomException("Please reload the page!")
            );
        if (connectedDevice.getUsingStatus().equals(UsingStatus.AVAILABLE)) {
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
        var notification = Notification.builder()
            .content(userDocument.getName() + " is connecting to " + connectedDevice.getName())
            .notificationType(NotificationType.USING_DEVICE)
            .isSeen(false)
            .build();
        websocketHandleEventService.sendNotificationExceptUser(userId, notification);
        return ConnectDeviceResponse.builder()
            .device(connectedDevice)
            .connectDeviceTime(new Date(System.currentTimeMillis()))
            .build();
    }

    @Override
    public void stopConnectToDevice(String deviceId, String userId) {
        validateIsNotNullOrEmpty(deviceId);
        validateIsNotNullOrEmpty(userId);
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, userId)) {
            throw new NotFoundCustomException("Not found device or user connect!");
        }
        websocketHandleEventService.sendMessageToDevice(userId, deviceId, Action.USER_DISCONNECT_TO_DEVICE);
    }

    @Override
    public void sendDeviceStatus(DeviceStatusRequest deviceStatusRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }
        Optional<ConnectedDeviceDocument> deviceDocumentOptional = connectedDeviceRepository
            .findById(deviceStatusRequest.getDeviceId());

        deviceDocumentOptional.ifPresent((deviceDocument) -> {
            if (deviceStatusRequest.getStatus().equals("AVAILABLE")) {
                deviceDocument.setUsingStatus(UsingStatus.AVAILABLE);
                deviceDocument.setCurrentUsingUser(null);
                connectedDeviceRepository.save(deviceDocument);
                websocketHandleEventService.sendListDeviceToAllUser();
                var notification = Notification.builder()
                    .notificationType(NotificationType.DEVICE_FREE)
                    .isSeen(false)
                    .content(deviceDocument.getName() + " is available access now!")
                    .build();
                websocketHandleEventService.sendNotificationAllUser(notification);
            } else if (deviceStatusRequest.getStatus().equals("BUSY")) {
                deviceDocument.setUsingStatus(UsingStatus.BUSY);
                deviceDocument.setCurrentUsingUser(null);
                connectedDeviceRepository.save(deviceDocument);
                websocketHandleEventService.sendListDeviceToAllUser();
            } else if (deviceStatusRequest.getStatus().equals("CAN_NOT_START") ||
                deviceStatusRequest.getStatus().equals("CAN_START")) {
                boolean isCanStart = deviceStatusRequest.getStatus().equals("CAN_START");
                var startMeasurementMessage = Message.builder()
                    .sender(deviceStatusRequest.getDeviceId())
                    .action(Action.START_MEASUREMENT)
                    .content(isCanStart)
                    .time(new Date(System.currentTimeMillis()))
                    .build();
                websocketHandleEventService.sendMessageToSpecificUser(
                    deviceStatusRequest.getUserId(),
                    startMeasurementMessage
                );
            }
        });
    }

    void validateIsNotNullOrEmpty(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationCustomException("Invalid id: " + id);
        }
    }
}
