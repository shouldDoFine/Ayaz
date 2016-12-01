package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MessageBroadcasterTest {

    private MessageBroadcaster broadcaster;
    private Map<String, User> userMap;
    private Map<String, UserSocketHandler> userSocketHandlerMap;

    @Before
    public final void before() {
        this.userMap = new HashMap<>();
        this.userSocketHandlerMap = new HashMap<>();
        this.broadcaster = new MessageBroadcaster(userMap, userSocketHandlerMap);
    }

    @Test
    public void shouldSendMessageWhenSimpleMessageTaken() throws Exception {
        userMap.put("Ayaz", mock(User.class));
        userMap.put("Alexandr", mock(User.class));
        UserSocketHandler receiverSocketHandler = mock(UserSocketHandler.class);
        userSocketHandlerMap.put("Ayaz", mock(UserSocketHandler.class));
        userSocketHandlerMap.put("Alexandr", receiverSocketHandler);
        UserMessage textMessage = new UserMessage("Ayaz", "Hello!");

        broadcaster.sendMessageToEveryone(textMessage);

        UserMessage expectedMessage = new UserMessage("Ayaz", "Ayaz: Hello!");
        verify(receiverSocketHandler, times(1)).sendMessage(expectedMessage);
    }
}