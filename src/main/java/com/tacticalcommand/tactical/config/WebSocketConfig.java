package com.tacticalcommand.tactical.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time tactical messaging.
 * Enables secure real-time communication between command centers and tactical units.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker for handling subscriptions and broadcasting messages.
     * 
     * @param config Message broker registry
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Enable simple broker for topics
        config.enableSimpleBroker("/topic", "/queue");
        
        // Set application destination prefix for messages bound to methods
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for personal messages
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints for WebSocket connections.
     * 
     * @param registry STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Register WebSocket endpoint with SockJS fallback
        registry.addEndpoint("/ws/messaging")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // Register endpoint for direct WebSocket connections (no SockJS)
        registry.addEndpoint("/ws/messaging")
                .setAllowedOriginPatterns("*");
    }
}
