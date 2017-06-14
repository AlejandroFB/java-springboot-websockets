package com.alejandro.mancala.player;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Player service manages and controls player's functionality.
 *
 * @author afernandez
 */
public class PlayerService {
    private Map<String, Player> players = new ConcurrentHashMap<>();

    /**
     * Creates a new player and stores it in the player's list.
     *
     * @param session The web socket sessions associated with the player
     * @param gameId The ID of the game the player is currently playing
     * @return The player just created
     */
    public Player createPlayer(WebSocketSession session, Integer gameId) {
        Player player = new Player(session, gameId);
        players.put(session.getId(), player);

        return player;
    }

    /**
     * Returns the player.
     *
     * @param sessionId The session ID associated with a player
     * @return The player
     */
    public Player getPlayer(String sessionId) {
        return players.get(sessionId);
    }

    /**
     * Remove a player from the list, meaning her associated connection was closed somehow.
     *
     * @param sessionId The session id
     * @return The player removed
     */
    public Player removePlayer(String sessionId) {
        return players.remove(sessionId);
    }
}