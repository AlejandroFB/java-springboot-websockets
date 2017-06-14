package com.alejandro.mancala;

import com.alejandro.mancala.game.Game;
import com.alejandro.mancala.game.GameService;
import com.alejandro.mancala.message.InputMessage;
import com.alejandro.mancala.message.MessageService;
import com.alejandro.mancala.message.TurnMessage;
import com.alejandro.mancala.player.Player;
import com.alejandro.mancala.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handles requests from clients via WebSocket.
 *
 * @author afernandez
 */
public class GameHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String OPPONENT_LEFT = "Your opponent left the game. Please refresh the browser to start again!";

    private ObjectMapper mapper = new ObjectMapper();

    private PlayerService playerService;
    private GameService gameService;
    private MessageService messageService;

    public GameHandler(PlayerService playerService, GameService gameService, MessageService messageService) {
        this.playerService = playerService;
        this.gameService = gameService;
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Connection established with Session ID: {}", session.getId());

        final Game game = gameService.matchGame();
        final Player player = playerService.createPlayer(session, game.getId());

        if (gameService.isFirstPlayerWaiting(game)) {
            game.setSecondPlayer(player);
            messageService.updatePlayersGameStarted(game);
        } else {
            game.setFirstPlayer(player);
        }
        gameService.addGame(game);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Connection closed with Session ID: {}", session.getId());

        final Player player = playerService.getPlayer(session.getId());
        final Player opponent = gameService.getOpponent(player);

        if (opponent != null) {
            messageService.sendMessage(opponent.getSession(), new TurnMessage(OPPONENT_LEFT));
        }

        gameService.removeGame(player.getGameId());
        playerService.removePlayer(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Processing message for Session ID: {}", session.getId());

        Player player = playerService.getPlayer(session.getId());
        Game game = gameService.getGame(player.getGameId());

        InputMessage inputMessage = mapper.readValue(message.getPayload(), InputMessage.class);

        boolean repeatTurn = gameService.moveStones(game, inputMessage);

        if (gameService.isGameFinished(game)) {
            gameService.calculateWinner(game);
            messageService.updatePlayersGameFinished(game);
        } else {
            messageService.updatePlayersNextTurn(game, session, repeatTurn);
        }
    }
}