package com.alejandro.mancala.message;

import java.io.Serializable;

/**
 * Base class for messages.
 *
 * @author afernandez
 */
public abstract class BaseMessage implements Serializable {
    private String turnMessage;
    private String player;

    public BaseMessage() {
    }

    public BaseMessage(String turnMessage) {
        this.turnMessage = turnMessage;
    }

    public BaseMessage(String turnMessage, String player) {
        this.turnMessage = turnMessage;
        this.player = player;
    }

    public String getTurnMessage() {
        return turnMessage;
    }

    public void setTurnMessage(String turnMessage) {
        this.turnMessage = turnMessage;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}