package com.alejandro.mancala.game;

import com.alejandro.mancala.player.Player;

import java.util.Objects;

/**
 * Controls the game's state and flows.
 *
 * @author afernandez
 */
public class Game {
    private Integer id;
    private State state;
    private Player firstPlayer;
    private Player secondPlayer;
    private Board board;
    private Player winner;

    public Game(Integer id) {
        this.id = id;
        this.state = State.PENDING;
        this.board = new Board();
    }

    public State getState() {
        return state;
    }

    public Integer getId() {
        return id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(Player secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Game)) {
            return false;
        }
        Game game = (Game) o;
        return this.id == game.id;
    }
}