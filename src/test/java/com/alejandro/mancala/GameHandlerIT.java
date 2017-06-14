package com.alejandro.mancala;

import com.alejandro.mancala.game.GameService;
import com.alejandro.mancala.message.InputMessage;
import com.alejandro.mancala.message.MessageService;
import com.alejandro.mancala.player.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Integration tests for GameHandler.
 *
 * @author afernandez
 */
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class GameHandlerIT {
    private static final String WEBSOCKET_PATH = "ws://localhost:9000/mancala";

    private ObjectMapper mapper = new ObjectMapper();

    private WebSocketContainer container;
    private TestWebSocketClient clientOne;
    private TestWebSocketClientTwo clientTwo;

    // 6 messages are received apart from the last one sent to one player when the other closes its session
    private CountDownLatch messageLatch = new CountDownLatch(6);
    private CountDownLatch closingLatch = new CountDownLatch(1);

    @Mock
    private TestCallback testCallback;

    @Before
    public void setUp() throws Exception {
        new GameHandler(new PlayerService(), new GameService(), new MessageService());

        container = ContainerProvider.getWebSocketContainer();
        clientOne = new TestWebSocketClient();
        clientTwo = new TestWebSocketClientTwo();
    }

    @Test
    public void testConnectTwoClientsAndSendOneMessagePerClient() throws Exception {
        container.connectToServer(clientOne, URI.create(WEBSOCKET_PATH));
        container.connectToServer(clientTwo, URI.create(WEBSOCKET_PATH));

        messageLatch.await(5, TimeUnit.SECONDS);

        clientOne.session.close();

        closingLatch.await(5, TimeUnit.SECONDS);

        // 7 responses processed: initials 2 to set up first turn and players, next 4 the responses from send() message
        // (two per client) and the last one is the message to let one player know her opponent left the game.
        verify(testCallback, timeout(1000).times(7)).calledMethod();
    }

    @ClientEndpoint
    public class TestWebSocketClient {
        Session session;

        @OnOpen
        public void onOpen(Session session) throws IOException {
            this.session = session;

            InputMessage message = new InputMessage();
            message.setPlayer("First Player");
            message.setPitSelected(2);
            message.setStones(6);

            session.getBasicRemote().sendText(mapper.writeValueAsString(message));
        }

        @OnClose
        public void onClose() {
            closingLatch.countDown();
        }

        @OnMessage
        public void processMessage(String message) throws IOException {
            System.out.println("Received message in Client One: " + message);

            testCallback.calledMethod();
            messageLatch.countDown();
        }
    }

    @ClientEndpoint
    public class TestWebSocketClientTwo {
        Session session;

        @OnOpen
        public void onOpen(Session session) throws IOException {
            this.session = session;

            InputMessage message = new InputMessage();
            message.setPlayer("Second Player");
            message.setPitSelected(4);
            message.setStones(6);

            session.getBasicRemote().sendText(mapper.writeValueAsString(message));
        }

        @OnClose
        public void onClose() {
            closingLatch.countDown();
        }

        @OnMessage
        public void processMessage(String message) {
            System.out.println("Received message in Client Two: " + message);

            testCallback.calledMethod();
            messageLatch.countDown();
        }
    }

    private class TestCallback {
        void calledMethod() {
        }
    }
}