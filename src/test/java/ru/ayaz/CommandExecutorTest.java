package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CommandExecutorTest {

    private CommandExecutor executor;
    private Map<String, User> userMap;
    private Map<String, UserSocketHandler> userSocketHandlerMap;
    private MessageBroadcaster broadcaster;

    @Before
    public final void before() {
        this.userMap = new HashMap<>();
        this.userSocketHandlerMap = new HashMap<>();
        this.broadcaster = mock(MessageBroadcaster.class);
        this.executor = new CommandExecutor(userMap, userSocketHandlerMap, broadcaster);
    }


    @Test
    public void shouldInvokeCloseStreamsWhenQuitCommandExecuted() throws Exception {
        UserSocketHandler userSocketHandler = mock(UserSocketHandler.class);
        userSocketHandlerMap.put("Ayaz", userSocketHandler);
        UserMessage quitCommand = new UserMessage("Ayaz", "#quit");

        executor.executeCommand(quitCommand);

        verify(userSocketHandler, times(1)).closeStreams();
    }


    @Test
    public void shouldRemoveFromUserMapWhenQuitCommandExecuted() throws Exception {
        userMap.put("Ayaz", mock(User.class));
        userSocketHandlerMap.put("Ayaz", mock(UserSocketHandler.class));
        UserMessage quitCommand = new UserMessage("Ayaz", "#quit");

        executor.executeCommand(quitCommand);

        assertFalse(userMap.containsKey("Ayaz"));
    }


    @Test
    public void shouldRemoveFromUserSocketHandlerMapWhenQuitCommandExecuted() throws Exception {
        userMap.put("Ayaz", mock(User.class));
        userSocketHandlerMap.put("Ayaz", mock(UserSocketHandler.class));
        UserMessage quitCommand = new UserMessage("Ayaz", "#quit");

        executor.executeCommand(quitCommand);

        assertFalse(userSocketHandlerMap.containsKey("Ayaz"));
    }


    @Test
    public void shouldIgnoreUserWhenIgnoreCommandExecuted() throws Exception {
        User user = new User("Ayaz");
        userMap.put("Ayaz", user);
        UserMessage ignoreCommand = new UserMessage("Ayaz", "#ignore spammer123");

        executor.executeCommand(ignoreCommand);

        assertTrue(user.isIgnored("spammer123"));
    }


    @Test
    public void shouldSendWarnMessageThroughBroadcasterWhenMalformedCommandExecuted() {
        UserMessage malformedCommand = new UserMessage("Ayaz", "#quiiiit");

        executor.executeCommand(malformedCommand);

        verify(broadcaster, times(1)).sendMessageToOnlyOne(new UserMessage("Ayaz", "Unknown command"), "Ayaz");
    }


    @Test
    public void shouldSendWarnMessageThroughBroadcasterWhenInvalidCommandExecuted() throws Exception {
        userMap.put("Ayaz", new User("Ayaz"));
        UserMessage invalidCommand = new UserMessage("Ayaz", "#ignore Ayaz");

        executor.executeCommand(invalidCommand);

        verify(broadcaster, times(1)).sendMessageToOnlyOne(new UserMessage("Ayaz", "Invalid command"), "Ayaz");
    }
}