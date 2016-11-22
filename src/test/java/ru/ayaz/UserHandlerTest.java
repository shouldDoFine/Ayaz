package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserHandlerTest {

    private UserHandler userHandler;

    @Before
    public final void before() throws InvalidNicknameException {
        User user = new User();
        user.setNickname("ayaz");
        this.userHandler = new UserHandler(user);
    }


    @Test
    public void shouldContainTwoMessagesInQueueWhenEnqueueTwoMessages() throws IOException {
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn("hi", "how are you?", null);
        UserMessageHandler messageHandler = new UserMessageHandler(500);
        this.userHandler.setMessageHandler(messageHandler);
        this.userHandler.setReader(reader);

        this.userHandler.run();

        UserMessage firstMessage = new UserMessage("ayaz", "hi");
        assertTrue(messageHandler.queueContainsMessage(firstMessage));
        UserMessage secondMessage = new UserMessage("ayaz", "how are you?");
        assertTrue(messageHandler.queueContainsMessage(secondMessage));
    }
}