package com.nvt.iot.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nvt.iot.model.ClientType;
import com.nvt.iot.payload.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Component
@Slf4j
public class CustomHandShakeInterceptor implements HandshakeInterceptor {
    @Value("${websocket.request.handshake.parameter.client-id}")
    private String CLIENT_ID;
    @Value("${websocket.request.handshake.parameter.client-type}")
    private String CLIENT_TYPE;

    @Override
    public boolean beforeHandshake(
        final ServerHttpRequest request,
        final ServerHttpResponse response,
        final WebSocketHandler wsHandler,
        final Map<String, Object> attributes
    ) throws Exception {
        final URI uri = request.getURI();
        final String query = uri.getQuery();
        
        log.info(uri.toString());
        if (query != null && uri.getPath().contains("/websocket")) {
            final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);

            String clientId = uriComponentsBuilder.build()
                .getQueryParams()
                .getFirst(CLIENT_ID);
            String clientType = uriComponentsBuilder.build()
                .getQueryParams()
                .getFirst(CLIENT_TYPE);
            if (clientId != null && clientType != null) {
                attributes.put(CLIENT_ID, clientId);
                attributes.put(CLIENT_TYPE, ClientType.valueOf(clientType));
                return true;
            }
        }
        sendUnAuthorizedResponse(response);
        return false;
    }

    private void sendUnAuthorizedResponse(ServerHttpResponse response) throws IOException {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        var errorResponse = ErrorResponse.builder()
            .statusCode(401)
            .message("Invalid token")
            .build();
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getBody().write(jsonResponse.getBytes());
    }

    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception
    ) {

    }
}
