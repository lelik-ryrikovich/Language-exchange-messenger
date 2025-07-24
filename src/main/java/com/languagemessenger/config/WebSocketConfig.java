package com.languagemessenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурационный класс для настройки WebSocket-соединений в приложении Language Messenger.
 * Использует STOMP-протокол для обеспечения обмена сообщениями в реальном времени, таких как чат.
 * Настраивает брокер сообщений и конечные точки для подключения клиентов.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Настраивает брокер сообщений и префиксы маршрутизации для обработки сообщений.
     * Устанавливает простой брокер для подписки на топики (например, /topic) и
     * определяет префикс /app для приложений, отправляющих сообщения.
     *
     * @param config реестр конфигурации брокера сообщений Spring
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Регистрирует конечные точки STOMP для WebSocket-соединений.
     * Добавляет endpoint /ws с поддержкой SockJS для обратной совместимости и
     * позволяет подключения с любых источников (setAllowedOriginPatterns("*")).
     *
     * @param registry реестр конечных точек STOMP для конфигурации
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}