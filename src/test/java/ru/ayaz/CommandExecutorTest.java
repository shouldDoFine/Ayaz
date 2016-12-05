package ru.ayaz;

import org.junit.Before;
import org.junit.Test;
import ru.ayaz.ru.ayaz.exceptions.InvalidUserCommandException;

import static org.mockito.Mockito.*;

public class CommandExecutorTest {

    private CommandExecutor executor;
    private User user;
    private UserSocketHandler userSocketHandler;

    @Before
    public final void before() throws Exception {
        this.user = mock(User.class);
        this.userSocketHandler = mock(UserSocketHandler.class);
        this.executor = new CommandExecutor(user, userSocketHandler);
    }


    @Test
    public void shouldInvokeCloseStreamsWhenQuitCommandExecuted() throws Exception {
        UserMessage quitCommand = new UserMessage("Ayaz", "#quit");

        executor.executeCommand(quitCommand);

        verify(userSocketHandler).closeStreams();
    }


    @Test
    public void shouldIgnoreUserWhenIgnoreCommandExecuted() throws Exception {
        UserMessage ignoreCommand = new UserMessage("Ayaz", "#ignore spammer123");

        executor.executeCommand(ignoreCommand);

        verify(user).ignoreUser("spammer123");
    }


    @Test
    public void shouldSendWarnMessageWhenMalformedCommandExecuted() {
        UserMessage malformedCommand = new UserMessage("Ayaz", "#quiiiit");

        executor.executeCommand(malformedCommand);

        verify(userSocketHandler).sendMessage(new UserMessage("Ayaz", "Unknown command"));
    }


    @Test
    public void shouldSendWarnMessageWhenInvalidCommandExecuted() throws Exception {
        doThrow(InvalidUserCommandException.class).when(user).ignoreUser("Ayaz");
        UserMessage invalidCommand = new UserMessage("Ayaz", "#ignore Ayaz");

        executor.executeCommand(invalidCommand);

        verify(userSocketHandler).sendMessage(new UserMessage("Ayaz", "Invalid command"));
    }
}