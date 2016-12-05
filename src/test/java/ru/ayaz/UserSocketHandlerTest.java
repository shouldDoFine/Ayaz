package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserSocketHandlerTest {

    private ChatRoom room;

    @Before
    public final void before() {
        BlockingQueue<UserMessage> messageQueue = new ArrayBlockingQueue(500, true);
        this.room = new ChatRoom(messageQueue);
    }

    @Test
    public void shouldContainMessageInQueueWhenSimpleMessageReadFromStreamAndEnqueued() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\nhi everyone").getBytes()));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);
        room.registerAtChatRoom(new User("ayaz"), userSocketHandler);

        userSocketHandler.run();

        assertTrue(room.queueContainsMessage(new UserMessage("ayaz", "hi everyone")));
    }

    @Test
    public void shouldWriteInvalidCommandWhenCommandReadFromStream() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\n#ignore ayaz").getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(outputStream);
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);
        room.registerAtChatRoom(userSocketHandler.getUser(), userSocketHandler);

        userSocketHandler.run();

        assertTrue(outputStream.toString().contains("Invalid command"));
    }
}