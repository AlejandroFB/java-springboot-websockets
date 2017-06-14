package com.alejandro.mancala.message;

import com.alejandro.mancala.game.Game;
import com.alejandro.mancala.game.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Service class that provides send methods used in the game.
 *
 * @author afernandez
 */
public class MessageService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String WAIT_TURN     = "Wait for your turn!";
    private static final String YOUR_TURN     = "Your turn!";
    private static final String ADD_TURN      = "You have got an additional turn!";
    private static final String YOU_WON       = "You have won the game! Congratulations!";
    private static final String YOU_LOST      = "You have lost the game. Try it next time again!";
    private static final String GAME_DRAW     = "The game ended in draw. Try it again next time!";

    public static final String FIRST_PLAYER  = "First Player";
    public static final String SECOND_PLAYER = "Second Player";

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Sends a message to both players regarding the next turn.
     *
     * @param game The specific game
     * @param repeatTurn True if the current player has an additional turn, false otherwise
     */
    public void updatePlayersNextTurn(Game game, WebSocketSession originalSession, boolean repeatTurn) {
        final WebSocketSession firstPlayerSession = game.getFirstPlayer().getSession();
        final WebSocketSession secondPlayerSession = game.getSecondPlayer().getSession();

        logger.info("Next turn. First player: {}, Second player: {}", firstPlayerSession.getId(), secondPlayerSession.getId());

        if (firstPlayerSession.getId().equals(originalSession.getId()) && repeatTurn) {
            sendFirstPlayerTurnMessage(firstPlayerSession, secondPlayerSession, game, repeatTurn);
        } else if (secondPlayerSession.getId().equals(originalSession.getId()) && !repeatTurn) {
            sendFirstPlayerTurnMessage(firstPlayerSession, secondPlayerSession, game, repeatTurn);
        } else {
            sendFirstPlayerWaitMessage(firstPlayerSession, secondPlayerSession, game, repeatTurn);
        }
    }

    /**
     * Sends a final message to updates players about the result of the game.
     *
     * @param game The game
     */
    public void updatePlayersGameFinished(Game game) {
        WebSocketSession sessionFirstPlayer = game.getFirstPlayer().getSession();
        WebSocketSession sessionSecondPlayer = game.getSecondPlayer().getSession();

        if (game.getState() == State.WINNER_FIRST_PLAYER) {
            sendMessage(sessionFirstPlayer, new OutputMessage(game.getBoard(), YOU_WON));
            sendMessage(sessionSecondPlayer, new OutputMessage(game.getBoard(), YOU_LOST));
        }

        if (game.getState() == State.WINNER_SECOND_PLAYER) {
            sendMessage(sessionFirstPlayer, new OutputMessage(game.getBoard(), YOU_LOST));
            sendMessage(sessionSecondPlayer, new OutputMessage(game.getBoard(), YOU_WON));
        }

        if (game.getState() == State.GAME_DRAW) {
            sendMessage(sessionFirstPlayer, new OutputMessage(game.getBoard(), GAME_DRAW));
            sendMessage(sessionSecondPlayer, new OutputMessage(game.getBoard(), GAME_DRAW));
        }
    }

    /**
     * Sends a message to both players that the game is full and has already started.
     *
     * @param game The specific game
     */
    public void updatePlayersGameStarted(Game game) {
        final WebSocketSession firstPlayerSession = game.getFirstPlayer().getSession();
        final WebSocketSession secondPlayerSession = game.getSecondPlayer().getSession();

        logger.info("Game has started. First player: {}, Second player: {}", firstPlayerSession.getId(), secondPlayerSession.getId());

        game.setState(State.STARTED);
        sendMessage(firstPlayerSession, new TurnMessage(FIRST_PLAYER, YOUR_TURN));
        sendMessage(secondPlayerSession, new TurnMessage(SECOND_PLAYER, WAIT_TURN));
    }

    /**
     * Send a message to the session passed as a parameter.
     *
     * @param session The session to send the message to
     * @param message The message
     */
    public void sendMessage(WebSocketSession session, BaseMessage message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void sendFirstPlayerWaitMessage(WebSocketSession playerOne, WebSocketSession playerTwo, Game game, boolean repeatTurn) {
        if (repeatTurn) {
            sendMessage(playerTwo, new OutputMessage(game.getBoard(), ADD_TURN));
        } else {
            sendMessage(playerTwo, new OutputMessage(game.getBoard(), YOUR_TURN));
        }
        sendMessage(playerOne, new OutputMessage(game.getBoard(), WAIT_TURN));
    }

    private void sendFirstPlayerTurnMessage(WebSocketSession playerOne, WebSocketSession playerTwo, Game game, boolean repeatTurn) {
        if (repeatTurn) {
            sendMessage(playerOne, new OutputMessage(game.getBoard(), ADD_TURN));
        } else {
            sendMessage(playerOne, new OutputMessage(game.getBoard(), YOUR_TURN));
        }
        sendMessage(playerTwo, new OutputMessage(game.getBoard(), WAIT_TURN));
    }
}