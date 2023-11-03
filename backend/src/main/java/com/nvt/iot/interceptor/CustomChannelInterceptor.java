//package com.nvt.iot.interceptor;
//
//import com.nvt.iot.exception.WebsocketNotFoundException;
//import com.nvt.iot.exception.WebsocketValidationException;
//import com.nvt.iot.model.ClientType;
//import com.nvt.iot.repository.DeviceRepository;
//import com.nvt.iot.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CustomChannelInterceptor implements ChannelInterceptor {
//    private static final String CLIENT_TYPE = "clientType";
//    private static final String CLIENT_ID = "clientId";
//    private final UserRepository userRepository;
//    private final DeviceRepository deviceRepository;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        log.info(accessor.getSessionAttributes().toString());
//        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
//            final String clientId = accessor.getFirstNativeHeader(CLIENT_ID);
//            final String clientTypeStr = accessor.getFirstNativeHeader(CLIENT_TYPE);
//
//            if (clientId == null || clientId.trim().isEmpty() ||
//                clientTypeStr == null || clientTypeStr.trim().isEmpty()) {
//                throw new WebsocketValidationException("Invalid id or sender type");
//            }
//
//            ClientType clientType = ClientType.valueOf(clientTypeStr);
//
//            if (clientType.equals(ClientType.USER) && !userRepository.existsById(clientId)) {
//                throw new WebsocketNotFoundException("Connection error user not found with id: " + clientId);
//            } else if (clientType.equals(ClientType.DEVICE) && !deviceRepository.existsById(clientId)) {
//                throw new WebsocketNotFoundException("Connection error device not found with id: " + clientId);
//            }
//
//            Map<String, Object> attributes = accessor.getSessionAttributes();
//            if (attributes != null) {
//                attributes.put("clientId", clientId);
//                attributes.put("clientType", clientType);
//            }
//        }
//        return message;
//    }
//}
