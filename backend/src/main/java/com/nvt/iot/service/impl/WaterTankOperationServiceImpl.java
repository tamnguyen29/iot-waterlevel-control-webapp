package com.nvt.iot.service.impl;

import com.nvt.iot.exception.WebsocketResourcesNotFoundException;
import com.nvt.iot.model.Action;
import com.nvt.iot.model.MeasurementStatus;
import com.nvt.iot.model.Message;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ControlUnitRepository;
import com.nvt.iot.service.WaterTankOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class WaterTankOperationServiceImpl implements WaterTankOperationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ControlUnitRepository controlUnitRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    @Value("${websocket.room.private-device}")
    private String DEVICE_PRIVATE_ROOM;
    @Value("${websocket.room.private-user}")
    private String USER_PRIVATE_ROOM;


    @Override
    public void startMeasurementWithControlParameter(String controllerId, String deviceId, String senderId) {
        if (!(controlUnitRepository.existsById(controllerId) &&
            connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, senderId))
        ) {
            throw new WebsocketResourcesNotFoundException(
                "Not found controller id or device id when starting measurement", senderId
            );
        }
        sendMeasurementMessageToDevice(senderId, deviceId, controllerId, Action.START_MEASUREMENT);
        sendMeasurementMessageBackToUser(senderId, MeasurementStatus.SUCCESS);
    }

    @Override
    public void stopMeasurement(String deviceId, String senderId) {
        if (!connectedDeviceRepository.existsByIdAndCurrentUsingUserId(deviceId, senderId)) {
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
        String controllerId,
        Action action
    ) {
        var message = Message.builder()
            .sender(String.format("USER[%s]", sender))
            .action(action)
            .content(controllerId)
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
}

