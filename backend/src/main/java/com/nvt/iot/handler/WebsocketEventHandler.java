package com.nvt.iot.handler;

import com.nvt.iot.document.ConnectedDeviceDocument;
import com.nvt.iot.document.ConnectedUserDocument;
import com.nvt.iot.exception.WebsocketResourcesNotFoundException;
import com.nvt.iot.model.Action;
import com.nvt.iot.model.ClientType;
import com.nvt.iot.model.Message;
import com.nvt.iot.model.UsingStatus;
import com.nvt.iot.repository.*;
import com.nvt.iot.service.WebsocketHandleEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebsocketEventHandler implements WebsocketHandleEventService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConnectedUserRepository connectedUserRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final UpdateWaterLevelRepository updateWaterLevelRepository;
    private final XControlRepository xControlRepository;
    @Value("${websocket.request.handshake.parameter.client-id}")
    private String CLIENT_ID;
    @Value("${websocket.request.handshake.parameter.client-type}")
    private String CLIENT_TYPE;
    @Value("${websocket.room.common}")
    private String CONNECTED_CLIENTS_ROOM;
    @Value(("${websocket.room.private-device}"))
    private String DEVICE_PRIVATE_ROOM;

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent connectEvent) {
        log.info("Connect: " + connectEvent.toString());
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(connectEvent.getMessage());
        Map<String, Object> attributes = accessor.getSessionAttributes();

        if (attributes == null || !attributes.containsKey(CLIENT_ID) || !attributes.containsKey(CLIENT_TYPE)) {
            throw new WebsocketResourcesNotFoundException(
                "Invalid id or type of client when trying to connect to server",
                Objects.requireNonNull(accessor.getUser()).getName()
            );
        }
        String id = (String) attributes.get(CLIENT_ID);
        ClientType clientType = (ClientType) attributes.get(CLIENT_TYPE);

        if (clientType.equals(ClientType.USER) && !connectedUserRepository.existsById(id)) {
            var user = userRepository.findById(id)
                .orElseThrow(() -> new WebsocketResourcesNotFoundException("Not found user id: " + id, id));
            var connectedUser = ConnectedUserDocument.builder()
                .id(id)
                .name(user.getFullName())
                .sessionId(accessor.getSessionId())
                .onlineAt(new Date(System.currentTimeMillis()))
                .build();
            connectedUserRepository.save(connectedUser);
        } else if (clientType.equals(ClientType.DEVICE)) {
            var device = deviceRepository.findById(id)
                .orElseThrow(() -> new WebsocketResourcesNotFoundException("Not found device id: " + id, id));
            var connectedDevice = ConnectedDeviceDocument.builder()
                .id(device.getId())
                .name(device.getName())
                .sessionId(accessor.getSessionId())
                .usingStatus(UsingStatus.AVAILABLE)
                .connectedAt(new Date(System.currentTimeMillis()))
                .build();
            connectedDeviceRepository.save(connectedDevice);
        }
    }

    @EventListener
    public void handleSessionDisConnectEvent(SessionDisconnectEvent disconnectEvent) {
        log.info("Disconnect: " + disconnectEvent.toString());
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(disconnectEvent.getMessage());
        String sessionId = accessor.getSessionId();
        ClientType client = checkIsUserOrDeviceBySessionId(sessionId);

        if (client.equals(ClientType.USER)) {
            ConnectedUserDocument connectedUserDocument = connectedUserRepository.findBySessionId(sessionId);
            var device = connectedDeviceRepository.findByCurrentUsingUserId(
                connectedUserDocument.getId()
            );
            if (device != null) {
                sendUserInfoToSpecificDevice(null, device.getId());
                device.setUsingStatus(UsingStatus.AVAILABLE);
                device.setCurrentUsingUser(null);
                connectedDeviceRepository.save(device);
            }
            updateWaterLevelRepository.deleteByUserId(connectedUserDocument.getId());
            deleteConnectedUserOrDeviceBySessionId(ClientType.USER, sessionId);
            sendListDeviceToAllUser();
            sendListUserToAllUser();
        }
        if (client.equals(ClientType.USER)) {
            ConnectedDeviceDocument device = connectedDeviceRepository.findBySessionId(sessionId);
            deleteConnectedUserOrDeviceBySessionId(ClientType.DEVICE, sessionId);
            sendListDeviceToAllUser();
            xControlRepository.deleteByDeviceId(device.getId());
        }
    }

    @EventListener
    public void handleSubscribeChannel(SessionSubscribeEvent sessionSubscribeEvent) {
        log.info(sessionSubscribeEvent.toString());
    }

    private ClientType checkIsUserOrDeviceBySessionId(String sessionId) {
        return (connectedDeviceRepository.existsBySessionId(sessionId)) ? ClientType.DEVICE
            : ClientType.USER;
    }


    @Override
    public List<?> getConnectedUsersOrDevicesList(ClientType type) {
        return (type.equals(ClientType.USER)) ?
            connectedUserRepository.findAll() : connectedDeviceRepository.findAll();
    }


    private void deleteConnectedUserOrDeviceBySessionId(ClientType client, String sessionId) {
        if (client.equals(ClientType.USER)) {
            connectedUserRepository.deleteBySessionId(sessionId);
        } else {
            connectedDeviceRepository.deleteBySessionId(sessionId);
        }
    }

    @Override
    public void sendListUserToAllUser() {
        var message = Message.builder()
            .action(Action.SEND_LIST_CONNECTED_USER)
            .content(getConnectedUsersOrDevicesList(ClientType.USER))
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSend(CONNECTED_CLIENTS_ROOM, message);
    }

    @Override
    public void sendListDeviceToAllUser() {
        var message = Message.builder()
            .action(Action.SEND_LIST_CONNECTED_DEVICE)
            .content(getConnectedUsersOrDevicesList(ClientType.DEVICE))
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSend(CONNECTED_CLIENTS_ROOM, message);
    }

    @Override
    public void sendUserInfoToSpecificDevice(String userId, String deviceName) {
        var message = Message.builder()
            .action(Action.USER_DISCONNECT_TO_DEVICE)
            .content(userId)
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSendToUser(deviceName, DEVICE_PRIVATE_ROOM, message);
    }
}
