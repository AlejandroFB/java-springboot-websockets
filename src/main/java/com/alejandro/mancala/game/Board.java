package com.alejandro.mancala.game;

/**
 * Represents the game's board.
 *
 * @author afernandez
 */
public class Board {
    private int[] pitsFirstPlayer;
    private int[] pitsSecondPlayer;
    private int firstPlayerBigPit;
    private int secondPlayerBigPit;

    public Board() {
        pitsFirstPlayer = new int[] {6, 6, 6, 6, 6, 6};
        pitsSecondPlayer = new int[] {6, 6, 6, 6, 6, 6};
        firstPlayerBigPit = 0;
        secondPlayerBigPit = 0;
    }

    public int[] getPitsFirstPlayer() {
        return pitsFirstPlayer;
    }

    public void setPitsFirstPlayer(int[] pitsFirstPlayer) {
        this.pitsFirstPlayer = pitsFirstPlayer;
    }

    public int[] getPitsSecondPlayer() {
        return pitsSecondPlayer;
    }

    public void setPitsSecondPlayer(int[] pitsSecondPlayer) {
        this.pitsSecondPlayer = pitsSecondPlayer;
    }

    public int getFirstPlayerBigPit() {
        return firstPlayerBigPit;
    }

    public void setFirstPlayerBigPit(int firstPlayerBigPit) {
        this.firstPlayerBigPit = firstPlayerBigPit;
    }

    public int getSecondPlayerBigPit() {
        return secondPlayerBigPit;
    }

    public void setSecondPlayerBigPit(int secondPlayerBigPit) {
        this.secondPlayerBigPit = secondPlayerBigPit;
    }
}