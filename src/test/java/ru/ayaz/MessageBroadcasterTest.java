package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MessageBroadcasterTest {

    private MessageBroadcaster broadcaster;
    private CommandExecutor executor;

    @Before
    public final void before() throws Exception {
        Map<String, User> userMap = new HashMap<>();
        Map<String, UserSocketHandler> userSocketHandlerMap = new HashMap<>();
        userMap.put("Ayaz", new User("Ayaz"));
        userMap.put("spammer", new User("spammer"));
        broadcaster = spy(new MessageBroadcaster(userMap, userSocketHandlerMap));
        executor = new CommandExecutor(userMap, userSocketHandlerMap);
    }

    @Test
    public void shouldNotSendWhenIgnoredUser() throws Exception {
        executor.executeCommand(new UserMessage("Ayaz", "#ignore spammer"));

        broadcaster.sendMessageToEveryone(new UserMessage("spammer", "dudu"));

        verify(broadcaster, times(0)).sendTextToOnlyOne("dudu", "Ayaz");
    }
}