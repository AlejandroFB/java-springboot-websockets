package com.alejandro.mancala.message;

/**
 * Message received from the client with the pit selected and the stones to sow. It represents
 * a movement in the game.
 *
 * @author afernandez
 */
public class InputMessage extends BaseMessage {
    private int pitSelected;
    private int stones;

    public int getPitSelected() {
        return pitSelected;
    }

    public void setPitSelected(int pitSelected) {
        this.pitSelected = pitSelected;
    }

    public int getStones() {
        return stones;
    }

    public void setStones(int stones) {
        this.stones = stones;
    }
}