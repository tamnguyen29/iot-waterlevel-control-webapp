package com.nvt.iot.handler;

import com.nvt.iot.document.ConnectedDeviceDocument;
import com.nvt.iot.document.ConnectedUserDocument;
import com.nvt.iot.exception.WebsocketResourcesNotFoundException;
import com.nvt.iot.model.Action;
import com.nvt.iot.model.ClientType;
import com.nvt.iot.model.Message;
import com.nvt.iot.model.UsingStatus;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import com.nvt.iot.repository.DeviceRepository;
import com.nvt.iot.repository.UserRepository;
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
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebsocketEventHandler {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConnectedUserRepository connectedUserRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    @Value("${websocket.request.handshake.parameter.client-id}")
    private String CLIENT_ID;
    @Value("${websocket.request.handshake.parameter.client-type}")
    private String CLIENT_TYPE;

    @Value(("${websocket.room.private-user}"))
    private String USER_PRIVATE_ROOM;
    private final WebsocketHandleEventService websocketHandleEventService;

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
        Date time = new Date(System.currentTimeMillis());
        if (clientType.equals(ClientType.USER) && !connectedUserRepository.existsById(id)) {
            var user = userRepository.findById(id)
                .orElseThrow(() -> new WebsocketResourcesNotFoundException("Not found user id: " + id, id));
            var connectedUser = ConnectedUserDocument.builder()
                .id(id)
                .email(user.getEmail())
                .name(user.getFullName())
                .role(user.getRole().toString())
                .sessionId(accessor.getSessionId())
                .onlineAt(time)
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
                .description(device.getDescription())
                .connectedAt(time)
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
            connectedUserRepository.findBySessionId(sessionId)
                .ifPresent((connectedUserDocument -> {
                    connectedDeviceRepository.findByCurrentUsingUserId(
                        connectedUserDocument.getId()
                    ).ifPresent((deviceDocument -> {
                        deviceDocument.setUsingStatus(UsingStatus.AVAILABLE);
                        deviceDocument.setCurrentUsingUser(null);
                        connectedDeviceRepository.save(deviceDocument);
                        websocketHandleEventService.sendMessageToDevice(
                            connectedUserDocument.getId(),
                            deviceDocument.getId(),
                            Action.USER_DISCONNECT_TO_DEVICE
                        );
                        websocketHandleEventService.sendListDeviceToAllUser();
                    }));
                    deleteConnectedUserOrDeviceBySessionId(ClientType.USER, sessionId);
                    websocketHandleEventService.sendListUserToAllUser();
                }));
        }
        if (client.equals(ClientType.DEVICE)) {
            connectedDeviceRepository.findBySessionId(sessionId)
                .ifPresent((deviceDocument -> {
                    if (deviceDocument.getCurrentUsingUser() != null) {
                        sendDisconnectMessageToCurrentUsingUser(
                            deviceDocument.getCurrentUsingUser().getId()
                        );
                    }
                    deleteConnectedUserOrDeviceBySessionId(ClientType.DEVICE, sessionId);
                    websocketHandleEventService.sendListDeviceToAllUser();
                }));

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


    private void deleteConnectedUserOrDeviceBySessionId(ClientType client, String sessionId) {
        if (client.equals(ClientType.USER)) {
            connectedUserRepository.deleteBySessionId(sessionId);
        } else {
            connectedDeviceRepository.deleteBySessionId(sessionId);
        }
    }

    private void sendDisconnectMessageToCurrentUsingUser(String userId) {
        var message = Message.builder()
            .sender("SERVER")
            .action(Action.DEVICE_DISCONNECT_UNEXPECTED)
            .time(new Date(System.currentTimeMillis()))
            .build();
        simpMessagingTemplate.convertAndSendToUser(userId, USER_PRIVATE_ROOM, message);
    }
}
