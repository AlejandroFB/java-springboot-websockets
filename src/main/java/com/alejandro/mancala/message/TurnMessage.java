package com.alejandro.mancala.message;

/**
 * Message used to send turn data between players.
 *
 * @author afernandez
 */
public class TurnMessage extends BaseMessage {

    public TurnMessage(String turnMessage) {
        super(turnMessage);
    }

    public TurnMessage(String player, String turnMessage) {
        super(turnMessage, player);
    }
}