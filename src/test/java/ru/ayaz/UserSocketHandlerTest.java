package ru.ayaz;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserSocketHandlerTest {

    @Test
    public void shouldContainMessageInQueueWhenReadFromStream() throws IOException, InvalidNicknameException {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(("hi everyone").getBytes()));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        UserMessageHandler messageHandler = new UserMessageHandler();
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, messageHandler);
        userSocketHandler.setNickname("ayaz");
        messageHandler.registerInMessageHandler(new User("ayaz"), userSocketHandler);

        userSocketHandler.run();

        assertTrue(messageHandler.queueContainsMessage(new UserMessage("ayaz", "hi everyone")));
    }
}