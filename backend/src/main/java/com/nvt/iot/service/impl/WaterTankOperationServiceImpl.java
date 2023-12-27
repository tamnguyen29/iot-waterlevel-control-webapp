package com.nvt.iot.service.impl;

import com.nvt.iot.document.*;
import com.nvt.iot.exception.InvalidProcessValueException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.mapper.ControlUnitDTOMapper;
import com.nvt.iot.model.*;
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

import java.util.*;

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
    private final ControlUnitDTOMapper controlUnitDTOMapper;
    private final MongoTemplate mongoTemplate;
    @Value("${websocket.room.private-device}")
    private String DEVICE_PRIVATE_ROOM;
    @Value("${websocket.room.private-user}")
    private String USER_PRIVATE_ROOM;

    @Override
    public void startMeasurement(String controllerId, String deviceId, String userId) {
        var controlUnitDoc = controlUnitRepository.findById(controllerId).orElseThrow(
            () -> new NotFoundCustomException("Not found control parameter")
        );

        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, userId)) {
            throw new NotFoundCustomException("Not found device with current user connected!");
        }

        Optional<DeviceControllerUserDocument> deviceControllerUserDocumentOpt = deviceControllerUserRepository
            .findByUserId(userId);

        deviceControllerUserDocumentOpt.ifPresentOrElse((deviceControllerUserDocument) -> {
            Map<String, List<ControlUnit>> deviceControlUnitMap = deviceControllerUserDocument.getDeviceIdControlUnit();
            if (deviceControlUnitMap.containsKey(deviceId)) {
                List<ControlUnit> controlUnitList = deviceControlUnitMap.get(deviceId);
                if (!existByControlUnit(controlUnitList, controlUnitDoc)) {
                    controlUnitList.add(controlUnitDTOMapper.apply(controlUnitDoc));
                    deviceControlUnitMap.put(deviceId, controlUnitList);
                }
            } else {
                deviceControlUnitMap.put(deviceId, List.of(controlUnitDTOMapper.apply(controlUnitDoc)));
            }
            deviceControllerUserDocument.setDeviceIdControlUnit(deviceControlUnitMap);
            deviceControllerUserRepository.save(deviceControllerUserDocument);
        }, () -> {
            Map<String, List<ControlUnit>> newMap = new HashMap<>();
            newMap.put(deviceId, List.of(controlUnitDTOMapper.apply(controlUnitDoc)));
            var deviceControllerUserDocument = DeviceControllerUserDocument.builder()
                .userId(userId)
                .deviceIdControlUnit(newMap)
                .build();
            deviceControllerUserRepository.save(deviceControllerUserDocument);
        });

        sendMeasurementMessageToDevice(userId,
            deviceId,
            controllerId,
            Action.START_MEASUREMENT
        );
    }

    boolean existByControlUnit(List<ControlUnit> controlUnitList, ControlUnitDocument controlUnit) {
        if (controlUnitList.size() > 0) {
            for (ControlUnit item : controlUnitList) {
                if (item.getId().equals(controlUnit.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void stopMeasurement(String deviceId, String senderId) {
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, senderId)) {
            throw new NotFoundCustomException("Not found device with current user connected!");
        }

        sendMeasurementMessageToDevice(senderId, deviceId, null, Action.STOP_MEASUREMENT);
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

    @Override
    public SignalControl getWaterLevelDataFromDevice(DataFromDevice data) {
        Date time = new Date(System.currentTimeMillis());
        sendDataToUser(data, time);

        var controlUnit = controlUnitRepository.findById(data.getControlUnitId())
            .orElseThrow(
                () -> new NotFoundCustomException("Control parameter not found with id " + data.getControlUnitId())
            );
        Optional<UpdateWaterLevelDocument> updateWaterLevelDocumentOtp = updateWaterLevelRepository
            .findByDeviceId(data.getDeviceId());
        updateWaterLevelDocumentOtp.ifPresentOrElse((document) -> {
            document.setValue(data.getValue());
            document.setTime(time);
            document.setDeviceId(data.getDeviceId());
            document.setControlParameter(
                new ControlParameter(controlUnit.getKp(), controlUnit.getSetpoint())
            );
            updateWaterLevelRepository.save(document);
        }, () -> waterLevelMeasurementHelperService.createFirstWaterLevelUpdate(
            data.getValue(),
            time,
            data.getDeviceId(),
            new ControlParameter(controlUnit.getKp(), controlUnit.getSetpoint())
        ));

        XControlDocument sigNalControlDoc = xControlRepository.findByDeviceId(data.getDeviceId())
            .orElseThrow(() -> new NotFoundCustomException("Not found x-control with id " + data.getDeviceId()));
        if (sigNalControlDoc.getValue() == null || sigNalControlDoc.getValue() == -1) {
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
                    List.of(new WaterLevelData(data.getValue(), time))
                );

            } else {
                addWaterLevelData(
                    data.getUserId(),
                    data.getControlUnitId(),
                    data.getDeviceId(),
                    new WaterLevelData(data.getValue(), time)
                );

            }
            ControlUnitDocument controlUnitDocument = controlUnitRepository.getControlUnitDocumentById(data.getControlUnitId());
            return SignalControl.builder()
                .xControl(sigNalControlDoc.getValue())
                .setpoint(controlUnitDocument.getSetpoint())
                .build();
        }
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
    public double sendFirstData(DataFromDevice data) {
        sendDataToUser(data, new Date(System.currentTimeMillis()));
        return -1;
    }
}

