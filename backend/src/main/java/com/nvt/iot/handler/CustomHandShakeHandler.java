package com.nvt.iot.handler;

import com.sun.security.auth.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
@Slf4j
public class CustomHandShakeHandler extends DefaultHandshakeHandler{
    @Value("${websocket.request.handshake.parameter.client-id}")
    private String CLIENT_ID;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String clientId = (String) attributes.get(CLIENT_ID);
        return new UserPrincipal(clientId);
    }

}
