package com.nvt.iot.service.impl;

import com.nvt.iot.document.DeviceControllerUserDocument;
import com.nvt.iot.document.UpdateWaterLevelDocument;
import com.nvt.iot.document.XControlDocument;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.WebsocketResourcesNotFoundException;
import com.nvt.iot.model.Action;
import com.nvt.iot.model.MeasurementStatus;
import com.nvt.iot.model.Message;
import com.nvt.iot.model.WaterLevelData;
import com.nvt.iot.repository.*;
import com.nvt.iot.service.WaterTankOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterTankOperationServiceImpl implements WaterTankOperationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ControlUnitRepository controlUnitRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final UpdateWaterLevelRepository updateWaterLevelRepository;
    private final XControlRepository xControlRepository;
    private final DeviceControllerUserRepository deviceControllerUserRepository;
    @Value("${websocket.room.private-device}")
    private String DEVICE_PRIVATE_ROOM;
    @Value("${websocket.room.private-user}")
    private String USER_PRIVATE_ROOM;


    @Override
    public void startMeasurementWithControlParameter(String controllerId, String deviceId, String senderId) {
        if (!controlUnitRepository.existsById(controllerId)) {
            throw new WebsocketResourcesNotFoundException(
                "Not found controller id starting measurement",
                senderId
            );
        }

        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, senderId)) {
            throw new WebsocketResourcesNotFoundException(
                "Not found online user or connected device when starting measurement",
                senderId
            );
        }

        sendMeasurementMessageToDevice(senderId,
            deviceId,
            controllerId,
            Action.START_MEASUREMENT
        );
        sendMeasurementMessageBackToUser(senderId, MeasurementStatus.SUCCESS);
        Optional<DeviceControllerUserDocument> deviceControllerUserDocumentOpt = deviceControllerUserRepository
            .findByUserId(senderId);
        DeviceControllerUserDocument deviceControllerUserDoc;
        if (deviceControllerUserDocumentOpt.isPresent()) {
            deviceControllerUserDoc = deviceControllerUserDocumentOpt.get();
            List<String> deviceIdList = deviceControllerUserDoc.getDeviceIdList();
            List<String> controllerIdList = deviceControllerUserDoc.getControllerIdList();
            if (!deviceIdList.contains(deviceId)) {
                deviceIdList.add(deviceId);
                deviceControllerUserDoc.setDeviceIdList(deviceIdList);
            }
            if (!controllerIdList.contains(controllerId)) {
                controllerIdList.add(controllerId);
                deviceControllerUserDoc.setControllerIdList(controllerIdList);
            }
        } else {
            deviceControllerUserDoc = DeviceControllerUserDocument.builder()
                .userId(senderId)
                .controllerIdList(List.of(controllerId))
                .deviceIdList(List.of(deviceId))
                .build();
        }
        deviceControllerUserRepository.save(deviceControllerUserDoc);
    }

    @Override
    public void stopMeasurement(String deviceId, String senderId) {
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, senderId)) {
            sendMeasurementMessageBackToUser(senderId, MeasurementStatus.FAILURE);
            throw new WebsocketResourcesNotFoundException(
                "Not found connected device id or user id.", senderId
            );
        }

        sendMeasurementMessageToDevice(senderId, deviceId, null, Action.STOP_MEASUREMENT);
        sendMeasurementMessageBackToUser(senderId, MeasurementStatus.SUCCESS);
    }

    private void sendMeasurementMessageToDevice(
        String sender,
        String receiver,
        String controlUnitId,
        Action action
    ) {
        var message = Message.builder()
            .sender(String.format("USER[%s]", sender))
            .action(action)
            .content(controlUnitId)
            .time(new Date(System.currentTimeMillis()))
            .receiver(receiver)
            .build();
        simpMessagingTemplate.convertAndSendToUser(receiver, DEVICE_PRIVATE_ROOM, message);
    }

    private void sendMeasurementMessageBackToUser(String user, MeasurementStatus status) {
        var message = Message.builder()
            .sender("SERVER")
            .action(Action.START_MEASUREMENT)
            .content(status)
            .time(new Date(System.currentTimeMillis()))
            .receiver(user)
            .build();
        simpMessagingTemplate.convertAndSendToUser(user, USER_PRIVATE_ROOM, message);
    }

    @Override
    public double getWaterLevelDataFromDevice(WaterLevelData data) {
//        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(data.getDeviceId(), data.getUserId())) {
//            throw new NotFoundCustomException("Not found connected device id " + data.getDeviceId());
//        }
        var message = Message.builder()
            .sender(data.getDeviceId())
            .action(Action.SEND_WATER_LEVEL_DATA)
            .content(data.getValue())
            .time(new Date(System.currentTimeMillis()))
            .receiver(data.getUserId())
            .build();
        simpMessagingTemplate.convertAndSendToUser(
            data.getUserId(),
            USER_PRIVATE_ROOM,
            message
        );

        if (!(data.getControlUnitId().equals("NONE") || data.getUserId().equals("NONE") || data.getDeviceId().equals("NONE"))) {
            Optional<UpdateWaterLevelDocument> updateWaterLevelDocumentOtp = updateWaterLevelRepository
                .findByUserId(data.getUserId());
            updateWaterLevelDocumentOtp.ifPresentOrElse((document) -> {
                document.setValue(data.getValue());
                document.setDeviceId(data.getDeviceId());
                document.setControlUnitId(data.getControlUnitId());
                document.setCreatedAt(new Date(System.currentTimeMillis()));
                updateWaterLevelRepository.save(document);
            }, () -> {
                var updateWaterLevelDoc = UpdateWaterLevelDocument.builder()
                    .value(data.getValue())
                    .userId(data.getUserId())
                    .createdAt(new Date(System.currentTimeMillis()))
                    .controlUnitId(data.getControlUnitId())
                    .deviceId(data.getDeviceId())
                    .build();
                updateWaterLevelRepository.save(updateWaterLevelDoc);
            });
        }

        XControlDocument sigNalControlDoc = xControlRepository.findByDeviceId(data.getDeviceId())
            .orElseThrow(() -> new NotFoundCustomException("Not found x-control with id " + data.getDeviceId()));
        return sigNalControlDoc.getValue();
    }
}

