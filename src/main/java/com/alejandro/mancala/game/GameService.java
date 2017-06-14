package com.alejandro.mancala.game;

import com.alejandro.mancala.message.InputMessage;
import com.alejandro.mancala.message.MessageService;
import com.alejandro.mancala.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Game service manages and controls the games registered.
 *
 * @author afernandez
 */
public class GameService {
    private AtomicInteger gamesPlayed = new AtomicInteger();
    private Map<Integer, Game> games = new HashMap<>();

    /**
     * Looks for a game that's already started and waiting for a second player to join or otherwise creates a new one.
     *
     * @return The game found or created
     */
    public Game matchGame() {
        Integer gameId = games.keySet().stream()
                .filter(key -> games.get(key).getState() == State.PENDING)
                .findFirst()
                .orElse(null);

        return gameId == null ? new Game(gamesPlayed.addAndGet(1)) : games.get(gameId);
    }

    /**
     * Retrieves a game.
     *
     * @param id The ID of a game
     * @return The game retrieved
     */
    public Game getGame(Integer id) {
        return games.get(id);
    }

    /**
     * Adds a game to the game's map.
     *
     * @param game The game to add
     */
    public void addGame(Game game) {
        games.put(game.getId(), game);
    }

    /**
     * Returns whether a specific game has already a first player waiting for the game to get started.
     *
     * @param game The game
     * @return True if there is a first player, false otherwise
     */
    public boolean isFirstPlayerWaiting(Game game) {
        if (game != null) {
            return game.getFirstPlayer() != null;
        }
        return false;
    }

    /**
     * Returns the opponent of a given player.
     *
     * @param player The player
     * @return The opponent, NULL if the opponent already left the game.
     */
    public Player getOpponent(Player player) {
        Game game = games.get(player.getGameId());

        if (game == null) {
            return null;
        }
        if (game.getFirstPlayer() != null && !game.getFirstPlayer().equals(player)) {
            return game.getFirstPlayer();
        }
        if (game.getSecondPlayer() != null && !game.getSecondPlayer().equals(player)) {
            return game.getSecondPlayer();
        }

        return null;
    }

    /**
     * Removes a game from the games's map.
     *
     * @param gameId The ID of the game to remove
     */
    public void removeGame(Integer gameId) {
        Game game = games.get(gameId);

        if (game != null) {
            game.setFirstPlayer(null);
            game.setSecondPlayer(null);

            games.remove(game.getId());
        }
    }

    /**
     * Performs a game move given the pit selected by the player and the stones that were in the pit.
     *
     * @param game The game
     * @param message The message with the amount of stones and pit selected
     * @return True if the move was a valid one to gain an additional turn, false otherwise
     */
    public boolean moveStones(Game game, InputMessage message) {
        boolean repeatTurn = false;
        int stones = message.getStones();
        int pit = message.getPitSelected();
        int nextPit = pit + 1;

        int [] pits;
        if (MessageService.FIRST_PLAYER.equals(message.getPlayer())) {
            pits = game.getBoard().getPitsFirstPlayer();
            increaseBigPitFirstPlayer(game, stones, pit);
        } else {
            pits = game.getBoard().getPitsSecondPlayer();
            increaseBigPitSecondPlayer(game, stones, pit);
        }

        for (int i = 0; i < stones; i++) {
            if (nextPit == 6) {
                if (i == stones - 1) {
                    repeatTurn = true;
                }
                break;
            }
            pits[nextPit++] += 1;
        }
        pits[pit] = 0;

        return repeatTurn;
    }

    /**
     * Given a game, checks whether a game is considered finished because there is no stones remaining in
     * on of the player's pit.
     *
     * @param game The game
     * @return True if the games has ended, false otherwise
     */
    public boolean isGameFinished(Game game) {
        int [] pitsCompleted = new int[] {0, 0, 0, 0, 0, 0};

        if (Arrays.equals(pitsCompleted, game.getBoard().getPitsFirstPlayer())) {
            return true;
        }

        if (Arrays.equals(pitsCompleted, game.getBoard().getPitsSecondPlayer())) {
            return true;
        }
        return false;
    }

    /**
     * Give a specific game, calculates the winner and updates the board to show the final count.
     *
     * @param game The game
     */
    public void calculateWinner(Game game) {
        int totalStonesFirstPlayer = 0;
        int totalStonesSecondPlayer = 0;

        int [] pitsFirstPlayer = game.getBoard().getPitsFirstPlayer();
        int [] pitsSecondPlayer = game.getBoard().getPitsSecondPlayer();

        for (int i = 0; i < 6; i++) {
            totalStonesFirstPlayer += pitsFirstPlayer[i];
            totalStonesSecondPlayer += pitsSecondPlayer[i];

            pitsFirstPlayer[i] = 0;
            pitsSecondPlayer[i] = 0;
        }

        totalStonesFirstPlayer += game.getBoard().getFirstPlayerBigPit();
        totalStonesSecondPlayer += game.getBoard().getSecondPlayerBigPit();

        game.getBoard().setFirstPlayerBigPit(totalStonesFirstPlayer);
        game.getBoard().setSecondPlayerBigPit(totalStonesSecondPlayer);

        if (totalStonesFirstPlayer > totalStonesSecondPlayer) {
            game.setState(State.WINNER_FIRST_PLAYER);
        } else if (totalStonesSecondPlayer > totalStonesFirstPlayer) {
            game.setState(State.WINNER_SECOND_PLAYER);
        } else {
            game.setState(State.GAME_DRAW);
        }
    }

    private void increaseBigPitFirstPlayer(Game game, int stones, int pit) {
        if (stones == 6 || stones > 6 - (pit + 1)) {
            int bigPit = game.getBoard().getFirstPlayerBigPit();
            game.getBoard().setFirstPlayerBigPit(++bigPit);
        }
    }

    private void increaseBigPitSecondPlayer(Game game, int stones, int pit) {
        if (stones == 6 || stones > 6 - (pit + 1)) {
            int bigPit = game.getBoard().getSecondPlayerBigPit();
            game.getBoard().setSecondPlayerBigPit(++bigPit);
        }
    }
}