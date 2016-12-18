package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UserSocketHandlerTest {

    private ChatRoom room;

    @Before
    public final void before() {
        this.room = mock(ChatRoom.class);
    }

    @Test
    public void shouldContainMessageInQueueWhenSimpleMessageReadFromStreamAndEnqueued() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\nhi everyone").getBytes()));
        when(socket.getOutputStream()).thenReturn(mock(ByteArrayOutputStream.class));
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);

        userSocketHandler.run();

        verify(room).enqueueMessage(new UserMessage("ayaz", "hi everyone"));
    }

    @Test
    public void shouldWriteInvalidCommandWhenMalformedCommandReadFromStream() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\n#ignore ayaz").getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(outputStream);
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);

        userSocketHandler.run();

        assertTrue(outputStream.toString().contains("Invalid command"));
    }

    @Test
    public void shouldCloseStreamsWhenQuitCommandReadFromStream() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayInputStream inputStream = spy(new ByteArrayInputStream(("ayaz\n#quit").getBytes()));
        when(socket.getInputStream()).thenReturn(inputStream);
        ByteArrayOutputStream outputStream = mock(ByteArrayOutputStream.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);

        userSocketHandler.run();

        verify(inputStream).close();
        verify(outputStream).close();
    }

    @Test
    public void shouldSendSuccessfulIgnoreWhenIgnoreCommandReadFromStream() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\n#ignore spammer").getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(outputStream);
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);

        userSocketHandler.run();

        assertTrue(outputStream.toString().contains("User successfully ignored"));
    }

    @Test
    public void shouldSendSuccessfulIgnoreWhenIgnoreCommandWithSeveralArgumentsReadFromStream() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\n#ignore spammer badGuy").getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(outputStream);
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);

        userSocketHandler.run();

        assertTrue(outputStream.toString().contains("User successfully ignored"));
    }

    @Test
    public void shouldSendUnknownCommandWhenUnknownCommandReadFromStream() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\n#setFontSize").getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(outputStream);
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);

        userSocketHandler.run();

        assertTrue(outputStream.toString().contains("Unknown command"));
    }

    @Test
    public void shouldGetUserWhenNicknameReadFromStream() throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("ayaz\n").getBytes()));
        when(socket.getOutputStream()).thenReturn(mock(ByteArrayOutputStream.class));
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);
        User expectedUser = new User("ayaz");

        userSocketHandler.run();

        assertEquals(expectedUser.getNickname(), userSocketHandler.getUser().getNickname());
    }
}