package com.nvt.iot.config;

import com.nvt.iot.handler.CustomHandShakeHandler;
import com.nvt.iot.interceptor.CustomHandShakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    private final CustomHandShakeInterceptor customHandShakeInterceptor;
    private final CustomHandShakeHandler customHandShakeHandler;
    @Value("${application.allow.origin}")
    private String ALLOW_ORIGIN;
    @Value("${application.allow.local-origin}")
    private String LOCAL_ALLOW_ORIGIN;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
//            .setHeartbeatValue(new long[]{10000, 10000})
//            .setTaskScheduler(heartBeatScheduler());
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/client");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins(ALLOW_ORIGIN, LOCAL_ALLOW_ORIGIN)
            .addInterceptors(customHandShakeInterceptor)
            .setHandshakeHandler(customHandShakeHandler)
            .withSockJS();
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
