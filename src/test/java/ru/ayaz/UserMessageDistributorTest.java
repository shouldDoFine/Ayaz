package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class UserMessageDistributorTest {

    private UserMessageDistributor messageDistributor;

    @Before
    public final void before() {
        this.messageDistributor = spy(new UserMessageDistributor());
    }

    @Test
    public void shouldBeAbleToEnqueueMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");

        messageDistributor.enqueueMessage(someMessage);

        assertTrue(messageDistributor.queueContainsMessage(someMessage));
    }


    @Test
    public void shouldBeAbleToGetEnqueuedMessage() throws InterruptedException {
        UserMessage enqueuedMessage = new UserMessage("Ayaz", "Hello");
        messageDistributor.enqueueMessage(enqueuedMessage);

        UserMessage gottenMessage = messageDistributor.takeMessage();

        assertEquals(enqueuedMessage, gottenMessage);
    }


    @Test
    public void shouldWarnAboutUnknownCommandWhenMalformedCommandTaken() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream output = new ByteArrayOutputStream(100);
        when(socket.getOutputStream()).thenReturn(output);
        when(socket.getInputStream()).thenReturn(mock(ByteArrayInputStream.class));
        UserSocketHandler receiverSocketHandler = new UserSocketHandler(socket, messageDistributor);
        messageDistributor.registerAtMessageDistributor(new User("Ayaz"), receiverSocketHandler);
        UserMessage malformedCommand = new UserMessage("Ayaz", "#quiiiit");
        doReturn(malformedCommand).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        assertTrue(output.toString().contains("Unknown command"));
    }


    @Test
    public void shouldWriteToReceiverWhenSimpleMessageTaken() throws Exception {
        Socket receiverSocket = mock(Socket.class);
        ByteArrayOutputStream receiverOutput = new ByteArrayOutputStream(100);
        when(receiverSocket.getOutputStream()).thenReturn(receiverOutput);
        when(receiverSocket.getInputStream()).thenReturn(mock(ByteArrayInputStream.class));
        UserSocketHandler receiverSocketHandler = new UserSocketHandler(receiverSocket, messageDistributor);
        messageDistributor.registerAtMessageDistributor(new User("receiverNickname"), receiverSocketHandler);
        UserMessage message = new UserMessage("senderNickname", "Hello everyone!");
        doReturn(message).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        assertTrue(receiverOutput.toString().contains("Hello everyone!"));
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
    public void shouldInvokeCloseStreamsWhenQuitCommandTaken() throws Exception {
        UserSocketHandler userSocketHandler = mock(UserSocketHandler.class);
        messageDistributor.registerAtMessageDistributor(new User("Ayaz"), userSocketHandler);
        UserMessage quitCommand = new UserMessage("Ayaz", "#quit");
        doReturn(quitCommand).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        verify(userSocketHandler, times(1)).closeStreams();
    }
}