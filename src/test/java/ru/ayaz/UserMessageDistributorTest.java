package ru.ayaz;

import org.junit.Before;
import org.junit.Test;
import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;
import ru.ayaz.ru.ayaz.exceptions.InvalidUserCommandException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class UserMessageDistributorTest {

    private UserMessageDistributor messageDistributor;
    private CommandExecutor executor;
    private MessageBroadcaster broadcaster;

    @Before
    public final void before() {
        HashMap<String, User> userMap = new HashMap<>();
        HashMap<String, UserSocketHandler> userSocketHandlerMap = new HashMap<>();
        BlockingQueue<UserMessage> messageQueue = new ArrayBlockingQueue(500, true);
        this.broadcaster = mock(MessageBroadcaster.class);
        this.executor = mock(CommandExecutor.class);
        this.messageDistributor = spy(new UserMessageDistributor(userMap, userSocketHandlerMap, messageQueue, broadcaster, executor));
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
    public void shouldDistributeMessageToExecutorWhenCommandTaken() throws Exception {
        UserMessage command = new UserMessage("Ayaz", "#quit");
        doReturn(command).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        verify(executor, times(1)).executeCommand(command);
    }


    @Test
    public void shouldDistributeMessageToBroadcasterWhenTextMessageTaken() throws Exception {
        UserMessage message = new UserMessage("Ayaz", "Hello everyone!");
        doReturn(message).doThrow(InterruptedException.class).when(messageDistributor).takeMessage();

        messageDistributor.run();

        verify(broadcaster, times(1)).sendMessageToEveryone(message);
    }
}