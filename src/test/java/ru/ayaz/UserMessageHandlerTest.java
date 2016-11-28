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

    private UserMessageDistributor messageDistributor;

    @Before
    public final void before() {
        messageDistributor = spy(new UserMessageDistributor());
    }

    @Test
    public void shouldBeAbleToEnqueueMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");

        messageDistributor.enqueueMessage(someMessage);

        assertTrue(messageDistributor.queueContainsMessage(someMessage));
    }

    @Test
    public void shouldBeAbleToGetEnqueuedMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");
        messageDistributor.enqueueMessage(someMessage);

        UserMessage gottenMessage = messageDistributor.takeMessage();

        assertEquals(someMessage, gottenMessage);
    }


    @Test
    public void shouldWarnAboutUnknownCommandWhenMalformedCommandTaken() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1]));
        doReturn(new UserMessage("Ayaz", "#quiit")).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();
        UserSocketHandler userSocketHandler = new UserSocketHandler(socket, messageDistributor);
        userSocketHandler.setNickname("Ayaz");
        messageDistributor.registerAtMessageDistributor(new User("Ayaz"), userSocketHandler);

        messageDistributor.run();

        assertTrue(outputStream.toString().contains("Unknown command"));
    }


    @Test
    public void shouldIgnoreUserWhenIgnoreCommandTaken() throws InvalidNicknameException, InterruptedException, InvalidUserCommandException {
        User user = new User("Ayaz");
        messageDistributor.registerAtMessageDistributor(user, mock(UserSocketHandler.class));
        UserMessage ignoreCommand = new UserMessage("Ayaz", "#ignore spammer123");
        doReturn(ignoreCommand).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        assertTrue(user.isIgnored("spammer123"));
    }


    @Test
    public void shouldWriteToReceiverWhenSimpleMessageTaken() throws Exception {
        Socket receiverSocket = mock(Socket.class);
        ByteArrayOutputStream receiverOutput = new ByteArrayOutputStream(100);
        when(receiverSocket.getOutputStream()).thenReturn(receiverOutput);
        when(receiverSocket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        UserSocketHandler receiverSocketHandler = new UserSocketHandler(receiverSocket, messageDistributor);
        messageDistributor.registerAtMessageDistributor(new User("Ayaz"), mock(UserSocketHandler.class));
        messageDistributor.registerAtMessageDistributor(new User("Alexandr"), receiverSocketHandler);
        UserMessage message = new UserMessage("Ayaz", "Hello everyone!");
        doReturn(message).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        assertTrue(receiverOutput.toString().contains("Hello everyone!"));
    }


    @Test
    public void shouldInvokeCloseStreamsWhenQuitCommandTaken() throws Exception {
        User user = spy(new User("Ayaz"));
        UserSocketHandler userSocketHandler = mock(UserSocketHandler.class);
        messageDistributor.registerAtMessageDistributor(user, userSocketHandler);
        UserMessage commandMessage = new UserMessage("Ayaz", "#quit");
        doReturn(commandMessage).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        verify(userSocketHandler, times(1)).closeStreams();
    }
}