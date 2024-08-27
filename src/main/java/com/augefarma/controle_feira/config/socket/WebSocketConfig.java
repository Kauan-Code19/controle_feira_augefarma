package com.augefarma.controle_feira.config.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    @Value("${allowed.origins}")
    private String allowedOrigins;

    /**
     * Configures the message broker for WebSocket communication.
     *
     * @param config the MessageBrokerRegistry object to configure the message broker settings
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enables a simple in-memory message broker for handling messages with destinations starting with "/topic"
        config.enableSimpleBroker("/topic");

        // Sets the prefix for application destinations where messages from clients will be routed
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers WebSocket endpoints and configures SockJS fallback support.
     *
     * @param registry the StompEndpointRegistry object to register WebSocket endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers a WebSocket endpoint at "/ws" and enables SockJS fallback options
        registry.addEndpoint("/realtime")
                // Allows connections from any origin (restrict origins in production for security)
                .setAllowedOrigins(allowedOrigins);
    }
}
