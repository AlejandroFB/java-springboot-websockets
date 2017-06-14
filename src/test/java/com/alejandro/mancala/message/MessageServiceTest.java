package com.alejandro.mancala.message;

import com.alejandro.mancala.game.Game;
import com.alejandro.mancala.game.State;
import com.alejandro.mancala.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MessageService.
 *
 * @author afernandez
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {
    private static final String WAIT_TURN     = "Wait for your turn!";
    private static final String YOUR_TURN     = "Your turn!";
    private static final String ADD_TURN      = "You have got an additional turn!";
    private static final String YOU_WON       = "You have won the game! Congratulations!";
    private static final String YOU_LOST      = "You have lost the game. Try it next time again!";
    private static final String GAME_DRAW     = "The game ended in draw. Try it again next time!";

    private MessageService messageService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private WebSocketSession session;
    @Mock
    private WebSocketSession sessionTwo;

    @Before
    public void setUp() throws Exception {
        messageService = new MessageService();
    }

    @Test
    public void updatePlayersNextTurnFirstPlayerWait() throws Exception {
        Game game = createMockGameWithPlayers();

        messageService.updatePlayersNextTurn(game, session, false);

        verify(sessionTwo).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), YOUR_TURN))));
        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), WAIT_TURN))));
    }

    @Test
    public void updatePlayersNextTurnFirstPlayerTurn() throws Exception {
        Game game = createMockGameWithPlayers();

        messageService.updatePlayersNextTurn(game, sessionTwo, false);

        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), YOUR_TURN))));
        verify(sessionTwo).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), WAIT_TURN))));
    }

    @Test
    public void updatePlayersNextTurnFirstPlayerTurnExtra() throws Exception {
        Game game = createMockGameWithPlayers();

        messageService.updatePlayersNextTurn(game, sessionTwo, true);

        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), WAIT_TURN))));
        verify(sessionTwo).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), ADD_TURN))));
    }

    @Test
    public void updatePlayersGameFinishedFirstPlayerWinner() throws Exception {
        Game game = createMockGameWithPlayers();
        game.setState(State.WINNER_FIRST_PLAYER);

        messageService.updatePlayersGameFinished(game);

        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), YOU_WON))));
        verify(sessionTwo).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), YOU_LOST))));
    }

    @Test
    public void updatePlayersGameFinishedSecondPlayerWinner() throws Exception {
        Game game = createMockGameWithPlayers();
        game.setState(State.WINNER_SECOND_PLAYER);

        messageService.updatePlayersGameFinished(game);

        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), YOU_LOST))));
        verify(sessionTwo).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), YOU_WON))));
    }

    @Test
    public void updatePlayersGameFinishedDraw() throws Exception {
        Game game = createMockGameWithPlayers();
        game.setState(State.GAME_DRAW);

        messageService.updatePlayersGameFinished(game);

        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), GAME_DRAW))));
        verify(sessionTwo).sendMessage(new TextMessage(objectMapper.writeValueAsString(new OutputMessage(game.getBoard(), GAME_DRAW))));
    }

    @Test
    public void updatePlayersGameStarted() throws Exception {
        Game game = createMockGameWithPlayers();

        messageService.updatePlayersGameStarted(game);

        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(new TurnMessage("First Player", YOUR_TURN))));
        verify(sessionTwo).sendMessage(new TextMessage(objectMapper.writeValueAsString(new TurnMessage("Second Player", WAIT_TURN))));
    }

    @Test
    public void sendMessage() throws Exception {
        InputMessage inputMessage = new InputMessage();
        inputMessage.setPlayer("First Player");
        inputMessage.setStones(6);
        inputMessage.setPitSelected(2);

        messageService.sendMessage(session, inputMessage);

        verify(session).sendMessage(new TextMessage(objectMapper.writeValueAsString(inputMessage)));
    }

    private Game createMockGameWithPlayers() {
        Game game = new Game(1);
        game.setFirstPlayer(new Player(session, 1));
        game.setSecondPlayer(new Player(sessionTwo, 1));

        when(session.getId()).thenReturn("1");
        when(sessionTwo.getId()).thenReturn("2");

        return game;
    }
}