package com.nvt.iot.service.impl;

import com.nvt.iot.document.ConnectedDeviceDocument;
import com.nvt.iot.document.ControlUnitDocument;
import com.nvt.iot.document.UpdateWaterLevelDocument;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.WebsocketResourcesNotFoundException;
import com.nvt.iot.model.*;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ControlUnitRepository;
import com.nvt.iot.repository.UpdateWaterLevelRepository;
import com.nvt.iot.repository.XControlRepository;
import com.nvt.iot.service.WaterTankOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterTankOperationServiceImpl implements WaterTankOperationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ControlUnitRepository controlUnitRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final UpdateWaterLevelRepository updateWaterLevelRepository;
    private final XControlRepository xControlRepository;
    private static final String UPDATE_WATER_LEVEL_DOC_ID = "6516899f5d385dddfafd7efa";
    private static final String SIGNAL_CONTROL_DOC_ID = "6549e02224ec870284d39c8e";
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
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(data.getDeviceId(), data.getUserId())) {
            throw new NotFoundCustomException("Not found connected device id " + data.getDeviceId());
        }
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
        ConnectedDeviceDocument device = connectedDeviceRepository.findByCurrentUsingUserId(data.getUserId());
        Optional<ControlUnitDocument> controlUnitOptional = controlUnitRepository.findById(data.getControlUnitId());

        var updateWaterLevelDoc = UpdateWaterLevelDocument.builder()
            .id(UPDATE_WATER_LEVEL_DOC_ID)
            .value(data.getValue())
            .creator(new Creator(
                data.getUserId(),
                device.getCurrentUsingUser().getName()
            ))
            .createdAt(new Date(System.currentTimeMillis()))
            .controlUnit(new ControlUnit(
                data.getControlUnitId(),
                controlUnitOptional.get().getName()
            ))
            .build();
        updateWaterLevelRepository.save(updateWaterLevelDoc);

        var sigNalControl = xControlRepository.findFirstById(SIGNAL_CONTROL_DOC_ID);
        return sigNalControl.getValue();
    }
}

