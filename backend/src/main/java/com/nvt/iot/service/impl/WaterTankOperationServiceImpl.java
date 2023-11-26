package com.nvt.iot.service.impl;

import com.nvt.iot.document.DeviceControllerUserDocument;
import com.nvt.iot.document.UpdateWaterLevelDocument;
import com.nvt.iot.document.XControlDocument;
import com.nvt.iot.exception.InvalidProcessValueException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.WebsocketResourcesNotFoundException;
import com.nvt.iot.model.*;
import com.nvt.iot.repository.*;
import com.nvt.iot.service.WaterLevelMeasurementHelperService;
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
    private final WaterLevelStoreRepository waterLevelStoreRepository;
    private final WaterLevelMeasurementHelperService waterLevelMeasurementHelperService;
    @Value("${websocket.room.private-device}")
    private String DEVICE_PRIVATE_ROOM;
    @Value("${websocket.room.private-user}")
    private String USER_PRIVATE_ROOM;
    @Value("${websocket.room.private-error}")
    private String ERROR_ROOM;


    @Override
    public void startMeasurementWithControlParameter(String controllerId, String deviceId, String senderId) {
        if (!controlUnitRepository.existsById(controllerId)) {
            throw new WebsocketResourcesNotFoundException(
                "Not found Control Parameter when starting measurement",
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
    public double getWaterLevelDataFromDevice(DataFromDevice data) {
        boolean isValidDeviceIdAndUserId = connectedDeviceRepository.existsByIdAndCurrentUsingUserId(data.getDeviceId(), data.getUserId());
        boolean isValidControlUnitId = controlUnitRepository.existsById(data.getControlUnitId());
        if (!(isValidDeviceIdAndUserId && isValidControlUnitId)) {
            String message = "Cannot find control parameter and something wrong in control process";
            sendErrorMessageToUser(message, data.getUserId());
            throw new NotFoundCustomException("One of the field in data is not valid!");
        }

        sendDataToUser(data.getValue(), data.getDeviceId(), data.getUserId());

        if (!xControlRepository.existsByDeviceId(data.getDeviceId())) {
            waterLevelMeasurementHelperService.createFirstXControl(data.getDeviceId());
        }

        Optional<UpdateWaterLevelDocument> updateWaterLevelDocumentOtp = updateWaterLevelRepository
            .findByUserId(data.getUserId());
        updateWaterLevelDocumentOtp.ifPresentOrElse((document) -> {
            document.setValue(data.getValue());
            document.setDeviceId(data.getDeviceId());
            document.setControlUnitId(data.getControlUnitId());
            document.setCreatedAt(new Date(System.currentTimeMillis()));
            updateWaterLevelRepository.save(document);
        }, () -> waterLevelMeasurementHelperService.createFirstWaterLevelUpdate(
            data.getUserId(),
            data.getDeviceId(),
            data.getControlUnitId()
        ));

        XControlDocument sigNalControlDoc = xControlRepository.findByDeviceId(data.getDeviceId())
            .orElseThrow(() -> new NotFoundCustomException("Not found x-control with id " + data.getDeviceId()));
        if (sigNalControlDoc.getValue() == -1) {
            throw new InvalidProcessValueException("Cannot get x-control value");
        } else {
            //Save data to WaterLevelStore
            if (!waterLevelStoreRepository.existsByUserIdAndDeviceIdAndControllerId(
                data.getUserId(),
                data.getDeviceId(),
                data.getControlUnitId()
            )) {
                waterLevelMeasurementHelperService.createFirstWaterLevelStore(
                    data.getUserId(),
                    data.getDeviceId(),
                    data.getControlUnitId(),
                    List.of(new WaterLevelData(data.getValue(), new Date(System.currentTimeMillis())))
                );

            } else {
                waterLevelStoreRepository.addWaterLevelData(
                    data.getUserId(),
                    data.getControlUnitId(),
                    data.getDeviceId(),
                    new WaterLevelData(data.getValue(), new Date(System.currentTimeMillis()))
                );
            }
        }
        return sigNalControlDoc.getValue();
    }

    private void sendDataToUser(double value, String deviceId, String userId) {
        var message = Message.builder()
            .sender(deviceId)
            .action(Action.SEND_WATER_LEVEL_DATA)
            .content(value)
            .time(new Date(System.currentTimeMillis()))
            .receiver(userId)
            .build();
        simpMessagingTemplate.convertAndSendToUser(
            userId,
            USER_PRIVATE_ROOM,
            message
        );
    }

    private void sendErrorMessageToUser(String message, String userId) {
        var sendMessage = Message.builder()
            .sender("SERVER")
            .content(message)
            .action(Action.ERROR)
            .time(new Date(System.currentTimeMillis()))
            .receiver(userId)
            .build();
        simpMessagingTemplate.convertAndSendToUser(userId, ERROR_ROOM, sendMessage);
    }

    @Override
    public double sendFirstData(DataFromDevice data) {
        sendDataToUser(data.getValue(), data.getDeviceId(), data.getUserId());
        return -1;
    }
}

