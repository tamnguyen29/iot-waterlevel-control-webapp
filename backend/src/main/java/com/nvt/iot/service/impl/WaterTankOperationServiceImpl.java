package com.nvt.iot.service.impl;

import com.nvt.iot.document.*;
import com.nvt.iot.exception.InvalidProcessValueException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.mapper.ControlUnitDTOMapper;
import com.nvt.iot.model.*;
import com.nvt.iot.payload.request.DeviceResetProcessRequest;
import com.nvt.iot.payload.request.FirstDataFromDevice;
import com.nvt.iot.payload.request.PumpOutRequest;
import com.nvt.iot.repository.*;
import com.nvt.iot.service.WaterLevelMeasurementHelperService;
import com.nvt.iot.service.WaterTankOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WaterTankOperationServiceImpl implements WaterTankOperationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ControlUnitRepository controlUnitRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final XControlRepository xControlRepository;
    private final DeviceControllerUserRepository deviceControllerUserRepository;
    private final WaterLevelMeasurementHelperService waterLevelMeasurementHelperService;
    private final DeviceRepository deviceRepository;
    private final MongoTemplate mongoTemplate;
    private final ControlUnitDTOMapper controlUnitDTOMapper;
    @Value("${websocket.room.private-device}")
    private String DEVICE_PRIVATE_ROOM;
    @Value("${websocket.room.private-user}")
    private String USER_PRIVATE_ROOM;

    @Override
    public void startMeasurement(String controllerId, String deviceId, String userId) {
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, userId)) {
            throw new NotFoundCustomException("Not found device with current user connected!");
        }
        var controlUnitDoc = controlUnitRepository.findById(controllerId).orElseThrow(
            () -> new NotFoundCustomException("Not found control parameter")
        );
        var deviceDoc = deviceRepository.findById(deviceId).orElseThrow(
            () -> new NotFoundCustomException("Not found device id")
        );

        deviceControllerUserRepository.findByUserId(userId)
                .ifPresentOrElse((controlDataDoc) -> {
                    List<ControlData> controlDataList = controlDataDoc.getControlDataList();
                    if (controlDataList != null && controlDataList.size() > 0) {
                        ControlData controlData = controlDataList.stream()
                            .filter((controlData1 -> controlData1.getDeviceId().equals(deviceId)))
                            .findFirst()
                            .orElse(null);
                        if (controlData != null) {
                            List<ControlUnit> controlUnitList = controlData.getControlUnitList();
                            boolean isControlUnitExist = controlUnitList.stream()
                                .anyMatch(controlUnit -> controlUnit.getId().equals(controllerId));
                            if (!isControlUnitExist) {
                                int index = controlDataList.indexOf(controlData);
                                controlUnitList.add(controlUnitDTOMapper.apply(controlUnitDoc));
                                controlData.setControlUnitList(controlUnitList);
                                controlDataList.set(index, controlData);
                            }
                        } else {
                            ControlData newControlData = ControlData.builder()
                                .deviceId(deviceDoc.getId())
                                .deviceName(deviceDoc.getName())
                                .deviceDescription(deviceDoc.getDescription())
                                .controlUnitList(List.of(controlUnitDTOMapper.apply(controlUnitDoc)))
                                .build();
                            controlDataList.add(newControlData);
                        }
                        controlDataDoc.setControlDataList(controlDataList);
                        deviceControllerUserRepository.save(controlDataDoc);
                    }
                }, () -> {
                    var newControlData = ControlData.builder()
                        .deviceId(deviceDoc.getId())
                        .deviceName(deviceDoc.getName())
                        .deviceDescription(deviceDoc.getDescription())
                        .controlUnitList(List.of(controlUnitDTOMapper.apply(controlUnitDoc)))
                        .build();
                    var dataControlDoc = DeviceControllerUserDocument.builder()
                        .userId(userId)
                        .controlDataList(List.of(newControlData))
                        .build();
                    deviceControllerUserRepository.save(dataControlDoc);
                });

        waterLevelMeasurementHelperService.createFirstWaterLevelStore(
                userId,
                deviceId,
                controllerId
        );
        sendMeasurementMessageToDevice(userId,
            deviceId,
            controlUnitDTOMapper.apply(controlUnitDoc),
            Action.START_MEASUREMENT
        );
    }


    @Override
    public void stopMeasurement(String deviceId, String senderId) {
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, senderId)) {
            throw new NotFoundCustomException("Not found device with current user connected!");
        }

        sendMeasurementMessageToDevice(senderId, deviceId, null, Action.STOP_MEASUREMENT);
    }

    private void sendMeasurementMessageToDevice(
        String userId,
        String receiver,
        ControlUnit controlUnit,
        Action action
    ) {
        var message = Message.builder()
            .sender(userId)
            .action(action)
            .content(controlUnit)
            .time(new Date(System.currentTimeMillis()))
            .receiver(receiver)
            .build();
        simpMessagingTemplate.convertAndSendToUser(receiver, DEVICE_PRIVATE_ROOM, message);
    }

    @Override
    public double getWaterLevelDataFromDevice(DataFromDevice data) {
        Date time = new Date(System.currentTimeMillis());
        sendDataToUser(data, time);
        updateWaterLevelDocument(data, time);
        XControlDocument sigNalControlDoc = xControlRepository.findByDeviceId(data.getDeviceId())
            .orElseThrow(() -> new NotFoundCustomException("Not found x-control with id " + data.getDeviceId()));
        if (sigNalControlDoc.getValue() == null || sigNalControlDoc.getValue() == 0 || sigNalControlDoc.getValue() == -1) {
            throw new InvalidProcessValueException("Cannot get x-control value");
        } else {
            addWaterLevelData(
                    data.getUserId(),
                    data.getControlUnitId(),
                    data.getDeviceId(),
                    new WaterLevelData(data.getValue(), time)
            );
            System.out.println("X-Control: " + sigNalControlDoc.getValue());
            return sigNalControlDoc.getValue();
        }
    }

    public void updateWaterLevelDocument(DataFromDevice data, Date time) {
        Query query = new Query(Criteria.where("deviceID").is(data.getDeviceId()));
        Update update = new Update()
                .set("water_level", data.getValue())
                .set("timestamp", time)
                .set("control_parameters", new ControlParameter(data.getKp(), data.getSetpoint()));

        mongoTemplate.upsert(query, update,UpdateWaterLevelDocument.class);
    }

    void addWaterLevelData(String userId, String controllerId, String deviceId, WaterLevelData waterLevelData) {
        Query query = new Query(Criteria.where("userID")
            .is(userId)
            .and("controllerID").is(controllerId)
            .and("deviceID").is(deviceId)
        );
        Update update = new Update().push("water_level", waterLevelData);
        mongoTemplate.updateFirst(query, update, WaterLevelStoreDocument.class);
    }

    private void sendDataToUser(DataFromDevice data, Date time) {
        var message = Message.builder()
            .sender(data.getDeviceId())
            .action(Action.SEND_WATER_LEVEL_DATA)
            .content(new WaterLevelData(data.getValue(), time))
            .time(time)
            .receiver(data.getUserId())
            .build();
        simpMessagingTemplate.convertAndSendToUser(
            data.getUserId(),
            USER_PRIVATE_ROOM,
            message
        );
    }

    @Override
    public double sendFirstData(FirstDataFromDevice data) {
        Date time = new Date(System.currentTimeMillis());
        var message = Message.builder()
            .action(Action.SEND_WATER_LEVEL_DATA)
            .content(new WaterLevelData(data.getValue(), time))
            .time(time)
            .receiver(data.getUserId())
            .build();
        simpMessagingTemplate.convertAndSendToUser(
            data.getUserId(),
            USER_PRIVATE_ROOM,
            message
        );
        return -1;
    }


    @Override
    public void sendPumpOutSignal(PumpOutRequest pumpOutRequest, BindingResult error) {
        if (error.hasErrors()) {
            throw new ValidationCustomException(error);
        }
        connectedDeviceRepository.findById(pumpOutRequest.getDeviceId())
            .ifPresentOrElse((deviceDocument) -> {
                CurrentUsingUser user = deviceDocument.getCurrentUsingUser();
                if (user == null || !user.getId().equals(pumpOutRequest.getUserId())) {
                    throw new NotFoundCustomException("Not found user is using device");
                }
                var message = Message.builder()
                    .sender(pumpOutRequest.getUserId())
                    .action(Action.SEND_PUMP_OUT_SIGNAL)
                    .content(pumpOutRequest.getPercentage())
                    .time(new Date(System.currentTimeMillis()))
                    .build();
                simpMessagingTemplate.convertAndSendToUser(
                    pumpOutRequest.getDeviceId(),
                    DEVICE_PRIVATE_ROOM,
                    message
                );
            }, () -> {
                throw new NotFoundCustomException("Not found connected device!");
            });
    }

    @Override
    public void sendRestartControlProcessToDevice(String deviceId, String userId) {
        connectedDeviceRepository.findById(deviceId)
            .ifPresentOrElse((connectedDeviceDocument -> {
                if(!connectedDeviceDocument.getCurrentUsingUser().getId().equals(userId)) {
                    throw new NotFoundCustomException("Not found user id " + userId);
                }
                var message = Message.builder()
                    .action(Action.RESTART_CONTROL_PROCESS)
                    .content(null)
                    .time(new Date(System.currentTimeMillis()))
                    .sender(userId)
                    .build();
                simpMessagingTemplate.convertAndSendToUser(deviceId, DEVICE_PRIVATE_ROOM, message);
            }), () -> {
                throw new NotFoundCustomException("Not found connected device with id " + deviceId);
            });
    }

    @Override
    public void sendRestartControlProcessToUser(DeviceResetProcessRequest request, BindingResult error) {
        if (error.hasErrors()) {
            throw new ValidationCustomException(error);
        }
        connectedDeviceRepository.findById(request.getDeviceId())
            .ifPresentOrElse((deviceDoc) -> {
                if (request.getStatus().equals("START_RESET")) {
                    deviceDoc.setUsingStatus(UsingStatus.RESET_PROCESS);
                    connectedDeviceRepository.save(deviceDoc);
                } else if (request.getStatus().equals("FINISH_RESET")) {
                    deviceDoc.setUsingStatus(UsingStatus.UNAVAILABLE);
                    connectedDeviceRepository.save(deviceDoc);
                    var message = Message.builder()
                        .sender(request.getDeviceId())
                        .action(Action.RESTART_CONTROL_PROCESS)
                        .content(request.getStatus())
                        .build();
                    simpMessagingTemplate.convertAndSendToUser(request.getUserId(), USER_PRIVATE_ROOM, message);
                }
            }, () -> {
                throw new NotFoundCustomException("Device id not found " + request.getDeviceId());
            });
    }
}

