package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserSocketHandlerTest {

    private UserMessageDistributor messageDistributor;

    @Before
    public final void before() {
        HashMap<String, User> userMap = new HashMap<>();
        HashMap<String, UserSocketHandler> userSocketHandlerMap = new HashMap<>();
        BlockingQueue<UserMessage> messageQueue = new ArrayBlockingQueue(500, true);
        MessageBroadcaster broadcaster = new MessageBroadcaster(userMap, userSocketHandlerMap);
        CommandExecutor executor = new CommandExecutor(userMap, userSocketHandlerMap, broadcaster);
        this.messageDistributor = new UserMessageDistributor(userMap, userSocketHandlerMap, messageQueue, broadcaster, executor);
    }

    @Test
    public void shouldContainMessageInQueueWhenReadFromStreamAndEnqueued() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("hi everyone").getBytes()));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, messageDistributor);
        userSocketHandler.setNickname("ayaz");
        messageDistributor.registerAtMessageDistributor(new User("ayaz"), userSocketHandler);

        userSocketHandler.run();

        assertTrue(messageDistributor.queueContainsMessage(new UserMessage("ayaz", "hi everyone")));
    }
}