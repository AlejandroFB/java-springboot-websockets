package com.alejandro.mancala.config;

import com.alejandro.mancala.GameHandler;
import com.alejandro.mancala.game.GameService;
import com.alejandro.mancala.message.MessageService;
import com.alejandro.mancala.player.PlayerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Web socket configuration.
 *
 * @author afernandez
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public WebSocketHandler gameWebSocketHandler() {
        return new GameHandler(new PlayerService(), new GameService(), new MessageService());
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler(), "/mancala");
    }
}