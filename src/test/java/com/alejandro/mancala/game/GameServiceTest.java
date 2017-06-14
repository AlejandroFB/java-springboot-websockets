package com.alejandro.mancala.game;

import com.alejandro.mancala.message.InputMessage;
import com.alejandro.mancala.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.socket.WebSocketSession;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GameService.
 *
 * @author afernandez
 */
@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    private GameService gameService;

    @Mock
    private WebSocketSession webSocketSession;

    @Before
    public void setUp() {
        gameService = new GameService();
    }

    @Test
    public void testMatchNewGame() throws Exception {
        Game game = gameService.matchGame();

        int [] initialPit = new int[] {6, 6, 6, 6, 6, 6};

        assertEquals(Integer.valueOf(1), game.getId());
        assertEquals(State.PENDING, game.getState());
        assertEquals(0, game.getBoard().getFirstPlayerBigPit());
        assertEquals(0, game.getBoard().getSecondPlayerBigPit());

        assertTrue(Arrays.equals(initialPit, game.getBoard().getPitsFirstPlayer()));
        assertTrue(Arrays.equals(initialPit, game.getBoard().getPitsSecondPlayer()));
    }

    @Test
    public void testAddNewAndMatchExistingGame() throws Exception {
        gameService.addGame(gameService.matchGame());
        Game game = gameService.matchGame();

        int [] initialPit = new int[] {6, 6, 6, 6, 6, 6};

        assertEquals(Integer.valueOf(1), game.getId());
        assertEquals(State.PENDING, game.getState());
        assertEquals(0, game.getBoard().getFirstPlayerBigPit());
        assertEquals(0, game.getBoard().getSecondPlayerBigPit());

        assertTrue(Arrays.equals(initialPit, game.getBoard().getPitsFirstPlayer()));
        assertTrue(Arrays.equals(initialPit, game.getBoard().getPitsSecondPlayer()));
    }

    @Test
    public void testFirstPlayerWaiting() throws Exception {
        Game game = gameService.matchGame();

        assertFalse(gameService.isFirstPlayerWaiting(null));
        assertFalse(gameService.isFirstPlayerWaiting(game));

        game.setFirstPlayer(new Player(webSocketSession, game.getId()));

        assertTrue(gameService.isFirstPlayerWaiting(game));
    }

    @Test
    public void testGetOpponent() throws Exception {
        Game game = createNewGame();
        gameService.addGame(game);

        assertNull(gameService.getOpponent(game.getFirstPlayer()));

        Player playerTwo = mock(Player.class);
        game.setSecondPlayer(mock(Player.class));

        when(playerTwo.getGameId()).thenReturn(1);

        assertNotNull(gameService.getOpponent(game.getFirstPlayer()));
    }

    @Test
    public void testRemoveGame() throws Exception {
        gameService.addGame(createNewGame());

        assertNotNull(gameService.getGame(1));

        gameService.removeGame(1);

        assertNull(gameService.getGame(1));
    }

    @Test
    public void testMoveStonesFirstPlayer() throws Exception {
        Game game = new Game(1);

        InputMessage inputMessage = new InputMessage();
        inputMessage.setPlayer("First Player");
        inputMessage.setPitSelected(2); // 0 to 5
        inputMessage.setStones(6);

        boolean extraTurn = gameService.moveStones(game, inputMessage);

        int [] result = new int[] {6, 6, 0, 7, 7, 7};
        int [] resultSecondPlayer = new int[] {6, 6, 6, 6, 6, 6};

        assertBoardPits(game, result, resultSecondPlayer, 1, 0);
        assertFalse(extraTurn);
    }

    @Test
    public void testMoveStonesSecondPlayer() throws Exception {
        Game game = new Game(1);

        InputMessage inputMessage = new InputMessage();
        inputMessage.setPlayer("Second Player");
        inputMessage.setPitSelected(5);
        inputMessage.setStones(6);

        boolean extraTurn = gameService.moveStones(game, inputMessage);

        int [] result = new int[] {6, 6, 6, 6, 6, 6};
        int [] resultSecondPlayer = new int[] {6, 6, 6, 6, 6, 0};

        assertBoardPits(game, result, resultSecondPlayer, 0, 1);
        assertFalse(extraTurn);
    }

    @Test
    public void testMoveStonesSecondPlayerExtraTurn() throws Exception {
        Game game = new Game(1);

        InputMessage inputMessage = new InputMessage();
        inputMessage.setPlayer("Second Player");
        inputMessage.setPitSelected(0);
        inputMessage.setStones(6);

        boolean extraTurn = gameService.moveStones(game, inputMessage);

        int [] result = new int[] {6, 6, 6, 6, 6, 6};
        int [] resultSecondPlayer = new int[] {0, 7, 7, 7, 7, 7};

        assertBoardPits(game, result, resultSecondPlayer, 0, 1);
        assertTrue(extraTurn);
    }

    @Test
    public void testIsGameFinished() throws Exception {
        Game game = new Game(1);

        assertFalse(gameService.isGameFinished(game));

        game.getBoard().setPitsFirstPlayer(new int[] {0, 0, 0, 0, 0, 0});
        assertTrue(gameService.isGameFinished(game));

        game.getBoard().setPitsFirstPlayer(new int[] {0, 7, 0, 8, 8, 8});
        game.getBoard().setPitsSecondPlayer(new int[] {0, 0, 0, 0, 0, 0});
        assertTrue(gameService.isGameFinished(game));
    }

    @Test
    public void testCalculateWinnerFirstPlayer() throws Exception {
        Game game = new Game(1);

        game.getBoard().setPitsFirstPlayer(new int[] {0, 7, 0, 8, 8, 8});
        game.getBoard().setFirstPlayerBigPit(2);

        game.getBoard().setPitsSecondPlayer(new int[] {0, 0, 0, 0, 0, 0});
        game.getBoard().setSecondPlayerBigPit(15);

        gameService.calculateWinner(game);

        assertTrue(Arrays.equals(game.getBoard().getPitsFirstPlayer(), new int[] {0, 0, 0, 0, 0, 0}));
        assertTrue(Arrays.equals(game.getBoard().getPitsSecondPlayer(), new int[] {0, 0, 0, 0, 0, 0}));

        assertEquals(33, game.getBoard().getFirstPlayerBigPit());
        assertEquals(15, game.getBoard().getSecondPlayerBigPit());

        assertEquals(State.WINNER_FIRST_PLAYER, game.getState());
    }

    @Test
    public void testCalculateWinnerSecondPlayer() throws Exception {
        Game game = new Game(1);

        game.getBoard().setPitsFirstPlayer(new int[] {0, 0, 0, 0, 0, 0});
        game.getBoard().setFirstPlayerBigPit(18);

        game.getBoard().setPitsSecondPlayer(new int[] {6, 6, 0, 4, 5, 5});
        game.getBoard().setSecondPlayerBigPit(1);

        gameService.calculateWinner(game);

        assertEquals(State.WINNER_SECOND_PLAYER, game.getState());
    }

    @Test
    public void testCalculateWinnerDraw() throws Exception {
        Game game = new Game(1);

        game.getBoard().setPitsFirstPlayer(new int[] {0, 0, 0, 0, 0, 0});
        game.getBoard().setFirstPlayerBigPit(15);

        game.getBoard().setPitsSecondPlayer(new int[] {0, 0, 0, 4, 5, 5});
        game.getBoard().setSecondPlayerBigPit(1);

        gameService.calculateWinner(game);

        assertEquals(State.GAME_DRAW, game.getState());
    }

    private void assertBoardPits(Game game, int [] firstPlayer, int [] secondPlayer,
                                 int firstPlayerBigBit, int secondPlayerBigPit) {

        assertTrue(Arrays.equals(game.getBoard().getPitsFirstPlayer(), firstPlayer));
        assertTrue(Arrays.equals(game.getBoard().getPitsSecondPlayer(), secondPlayer));

        assertEquals(firstPlayerBigBit, game.getBoard().getFirstPlayerBigPit());
        assertEquals(secondPlayerBigPit, game.getBoard().getSecondPlayerBigPit());
    }

    private Game createNewGame() {
        Game game = new Game(1);
        Player player = mock(Player.class);
        game.setFirstPlayer(player);

        when(player.getGameId()).thenReturn(1);

        return game;
    }
}