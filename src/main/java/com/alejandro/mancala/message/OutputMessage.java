package com.alejandro.mancala.message;

import com.alejandro.mancala.game.Board;

/**
 * Output message to update board in the client.
 *
 * @author afernandez
 */
public class OutputMessage extends BaseMessage {
    private int[] pitsFirstPlayer;
    private int[] pitsSecondPlayer;
    private int firstPlayerBigPit;
    private int secondPlayerBigPit;

    public OutputMessage() {
    }

    public OutputMessage(Board board, String turnMessage) {
        super(turnMessage);
        this.pitsFirstPlayer = board.getPitsFirstPlayer();
        this.pitsSecondPlayer = board.getPitsSecondPlayer();
        this.firstPlayerBigPit = board.getFirstPlayerBigPit();
        this.secondPlayerBigPit = board.getSecondPlayerBigPit();
    }

    public int[] getPitsFirstPlayer() {
        return pitsFirstPlayer;
    }

    public int[] getPitsSecondPlayer() {
        return pitsSecondPlayer;
    }

    public int getFirstPlayerBigPit() {
        return firstPlayerBigPit;
    }

    public int getSecondPlayerBigPit() {
        return secondPlayerBigPit;
    }
}