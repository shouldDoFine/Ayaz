package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class UserMessageHandlerTest {

    private UserMessageHandler messageHandler;

    @Before
    public final void before() {
        messageHandler = spy(new UserMessageHandler());
    }

    @Test
    public void shouldBeAbleToEnqueueMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");

        messageHandler.enqueueMessage(someMessage);

        assertTrue(messageHandler.queueContainsMessage(someMessage));
    }

    @Test
    public void shouldBeAbleToGetEnqueuedMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");
        messageHandler.enqueueMessage(someMessage);

        UserMessage gottenMessage = messageHandler.takeMessage();

        assertEquals(someMessage, gottenMessage);
    }


    @Test
    public void shouldWarnAboutUnknownCommandWhenMalformedCommandTaken() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1]));
        doReturn(new UserMessage("Ayaz", "#quiit")).doThrow(InterruptedException.class).when(messageHandler).takeMessage();
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, messageHandler);
        userSocketHandler.setNickname("Ayaz");
        messageHandler.registerInMessageHandler(new User("Ayaz"), userSocketHandler);

        messageHandler.run();

        assertTrue(outputStream.toString().contains("Unknown command"));
    }


    @Test
    public void shouldIgnoreUserWhenIgnoreCommandTaken() throws InvalidNicknameException, InterruptedException, InvalidUserCommandException {
        User user = new User("Ayaz");
        messageHandler.registerInMessageHandler(user, mock(UserSocketHandler.class));
        UserMessage ignoreCommand = new UserMessage("Ayaz", "#ignore spammer123");
        doReturn(ignoreCommand).doThrow(InterruptedException.class).when(messageHandler).takeMessage();

        messageHandler.run();

        assertTrue(user.isIgnored("spammer123"));
    }


    @Test
    public void shouldWriteToReceiverWhenSimpleMessageTaken() throws Exception {
        Socket receiverSocket = mock(Socket.class);
        ByteArrayOutputStream receiverOutput = new ByteArrayOutputStream(100);
        when(receiverSocket.getOutputStream()).thenReturn(receiverOutput);
        when(receiverSocket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        UserSocketHandler receiverSocketHandler = new UserSocketHandler(receiverSocket, messageHandler);
        messageHandler.registerInMessageHandler(new User("Ayaz"), mock(UserSocketHandler.class));
        messageHandler.registerInMessageHandler(new User("Alexandr"), receiverSocketHandler);
        UserMessage message = new UserMessage("Ayaz", "Hello everyone!");
        doReturn(message).doThrow(InterruptedException.class).when(messageHandler).takeMessage();

        messageHandler.run();

        assertTrue(receiverOutput.toString().contains("Hello everyone!"));
    }


    @Test
    public void shouldCloseStreamsWhenQuitCommandTaken() throws Exception {
        User user = spy(new User("Ayaz"));
        UserSocketHandler userSocketHandler = mock(UserSocketHandler.class);
        messageHandler.registerInMessageHandler(user, userSocketHandler);
        UserMessage commandMessage = new UserMessage("Ayaz", "#quit");
        doReturn(commandMessage).doThrow(InterruptedException.class).when(messageHandler).takeMessage();

        messageHandler.run();

        verify(userSocketHandler, times(1)).closeStreams();
    }
}