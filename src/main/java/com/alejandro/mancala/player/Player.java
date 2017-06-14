package com.alejandro.mancala.player;

import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

/**
 * Represents a game's player.
 *
 * @author afernandez
 */
public class Player {
    private WebSocketSession session;
    private Integer gameId;

    public Player(WebSocketSession session, Integer gameId) {
        this.session = session;
        this.gameId = gameId;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public Integer getGameId() {
        return gameId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.session.getId(), this.gameId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Player)) {
            return false;
        }
        Player player = (Player) o;
        return this.session.getId().equals(player.getSession().getId()) && this.gameId == player.gameId;
    }
}