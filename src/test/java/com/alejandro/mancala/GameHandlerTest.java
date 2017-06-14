package com.alejandro.mancala;

import com.alejandro.mancala.game.Game;
import com.alejandro.mancala.game.GameService;
import com.alejandro.mancala.message.InputMessage;
import com.alejandro.mancala.message.MessageService;
import com.alejandro.mancala.message.TurnMessage;
import com.alejandro.mancala.player.Player;
import com.alejandro.mancala.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GameHandler.
 *
 * @author afernandez
 */
@RunWith(MockitoJUnitRunner.class)
public class GameHandlerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private WebSocketSession session;
    @Mock
    private PlayerService playerService;
    @Mock
    private GameService gameService;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private GameHandler gameHandler;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testAfterConnectionEstablishedFirstPlayerJoined() throws Exception {
        Game game = new Game(1);
        Player player = new Player(session, game.getId());

        when(gameService.matchGame()).thenReturn(game);
        when(playerService.createPlayer(session, game.getId())).thenReturn(player);
        when(gameService.isFirstPlayerWaiting(game)).thenReturn(false);

        gameHandler.afterConnectionEstablished(session);

        verify(messageService, never()).updatePlayersGameStarted(game);
        verify(gameService).addGame(game);

        assertEquals(player, game.getFirstPlayer());
    }

    @Test
    public void testAfterConnectionEstablishedGameStarted() throws Exception {
        Game game = new Game(1);
        Player player = new Player(session, game.getId());

        when(gameService.matchGame()).thenReturn(game);
        when(playerService.createPlayer(session, game.getId())).thenReturn(player);
        when(gameService.isFirstPlayerWaiting(game)).thenReturn(true);

        gameHandler.afterConnectionEstablished(session);

        verify(messageService).updatePlayersGameStarted(game);
        verify(gameService).addGame(game);

        assertEquals(player, game.getSecondPlayer());
    }

    @Test
    public void testAfterConnectionClosed() throws Exception {
        Player player = new Player(session, 1);
        Player opponent = new Player(mock(WebSocketSession.class), 1);

        when(session.getId()).thenReturn("1");
        when(playerService.getPlayer("1")).thenReturn(player);
        when(gameService.getOpponent(player)).thenReturn(opponent);

        gameHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(messageService).sendMessage(eq(opponent.getSession()), any(TurnMessage.class));
        verify(gameService).removeGame(player.getGameId());
        verify(playerService).removePlayer("1");
    }

    @Test
    public void testAfterConnectionClosedOpponentNull() throws Exception {
        Player player = new Player(session, 1);

        when(session.getId()).thenReturn("1");
        when(playerService.getPlayer("1")).thenReturn(player);
        when(gameService.getOpponent(player)).thenReturn(null);

        gameHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(messageService, never()).sendMessage(any(WebSocketSession.class), any(TurnMessage.class));
        verify(gameService).removeGame(player.getGameId());
        verify(playerService).removePlayer("1");
    }

    @Test
    public void testHandleTextMessageGameFinished() throws Exception {
        Game game = new Game(1);
        Player player = new Player(session, game.getId());

        InputMessage inputMessage = new InputMessage();
        inputMessage.setPlayer("First Player");

        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(inputMessage));

        when(session.getId()).thenReturn("1");
        when(playerService.getPlayer("1")).thenReturn(player);
        when(gameService.getGame(player.getGameId())).thenReturn(game);
        when(gameService.isGameFinished(game)).thenReturn(true);

        gameHandler.handleTextMessage(session, textMessage);

        verify(gameService).calculateWinner(game);
        verify(messageService).updatePlayersGameFinished(game);
        verify(messageService, never()).updatePlayersNextTurn(game, session, false);
    }

    @Test
    public void testHandleTextMessageNextTurn() throws Exception {
        Game game = new Game(1);
        Player player = new Player(session, game.getId());

        InputMessage inputMessage = new InputMessage();
        inputMessage.setPlayer("First Player");

        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(inputMessage));

        when(session.getId()).thenReturn("1");
        when(playerService.getPlayer("1")).thenReturn(player);
        when(gameService.getGame(player.getGameId())).thenReturn(game);
        when(gameService.isGameFinished(game)).thenReturn(false);

        gameHandler.handleTextMessage(session, textMessage);

        verify(gameService, never()).calculateWinner(game);
        verify(messageService, never()).updatePlayersGameFinished(game);
        verify(messageService).updatePlayersNextTurn(game, session, false);
    }
}