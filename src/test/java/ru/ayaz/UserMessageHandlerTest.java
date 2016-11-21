package ru.ayaz;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class UserMessageHandlerTest {

    private UserMessageHandler messageHandler;

    @Before
    public final void before() {
        int queueSize = 500;
        messageHandler = new UserMessageHandler(queueSize);
    }

    @Test
    public void shouldPutMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");

        messageHandler.enqueueMessage(someMessage);

        assertTrue(messageHandler.queueContainsMessage(someMessage));
    }

    @Test
    public void shouldTakeMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");
        messageHandler.enqueueMessage(someMessage);

        UserMessage gottenMessage = messageHandler.takeMessage();

        assertEquals(someMessage, gottenMessage);
    }


    @Test
    public void shouldInvokePrintUnknownCommandWhenMaliformedCommandPassed() throws InvalidNicknameException, InterruptedException {
        User user = new User();
        user.setNickname("Ayaz");
        UserMessage commandMessage = new UserMessage("Ayaz", "#quiit");
        PrintWriter writer = mock(PrintWriter.class);
        UserMessageHandler spyMessageHandler = spy(messageHandler);
        spyMessageHandler.addUserToMap(user);
        spyMessageHandler.addWriterToMap(user.getNickname(), writer);
        doReturn(commandMessage).doThrow(InterruptedException.class).when(spyMessageHandler).takeMessage();

        spyMessageHandler.run();

        verify(writer, times(1)).println("Unknown command");
    }


    @Test
    public void shouldInvokeIgnoreUserWhenIgnoreCommandAppears() throws InvalidNicknameException, InterruptedException, InvalidUserCommandException{
        User user = spy(new User());
        user.setNickname("Ayaz");
        UserMessage commandMessage = new UserMessage("Ayaz", "#ignore spammer123");
        PrintWriter senderWriter = mock(PrintWriter.class);
        UserMessageHandler spyMessageHandler = spy(messageHandler);
        spyMessageHandler.addUserToMap(user);
        spyMessageHandler.addWriterToMap(user.getNickname(), senderWriter);
        doReturn(commandMessage).doThrow(InterruptedException.class).when(spyMessageHandler).takeMessage();

        spyMessageHandler.run();

        verify(user, times(1)).ignoreUser("spammer123");
    }


    @Test
    public void shouldInvokeReceiverWritersPrintWhenSimpleMessageAppears() throws InvalidNicknameException, InterruptedException{
        User userSender = new User();
        User firstUserReceiver = new User();
        User secondUserReceiver = new User();
        userSender.setNickname("Ayaz");
        firstUserReceiver.setNickname("Alexandr");
        secondUserReceiver.setNickname("Tyson");
        PrintWriter senderWriter = mock(PrintWriter.class);
        PrintWriter firstReceiverWriter = mock(PrintWriter.class);
        PrintWriter secondReceiverWriter = mock(PrintWriter.class);
        UserMessageHandler spyMessageHandler = spy(messageHandler);
        spyMessageHandler.addUserToMap(userSender);
        spyMessageHandler.addUserToMap(firstUserReceiver);
        spyMessageHandler.addUserToMap(secondUserReceiver);
        spyMessageHandler.addWriterToMap(userSender.getNickname(), senderWriter);
        spyMessageHandler.addWriterToMap(firstUserReceiver.getNickname(), firstReceiverWriter);
        spyMessageHandler.addWriterToMap(secondUserReceiver.getNickname(), secondReceiverWriter);
        UserMessage commandMessage = new UserMessage("Ayaz", "Hello everyone!");
        doReturn(commandMessage).doThrow(InterruptedException.class).when(spyMessageHandler).takeMessage();

        spyMessageHandler.run();

        verify(senderWriter, times(0)).println("Ayaz: Hello everyone!");
        verify(firstReceiverWriter, times(1)).println("Ayaz: Hello everyone!");
        verify(secondReceiverWriter, times(1)).println("Ayaz: Hello everyone!");
    }


    @Test
    public void shouldCloseConnectionWhenQuitCommandAppears() throws InvalidNicknameException, InterruptedException, IOException {
        User user = spy(new User());
        user.setNickname("Ayaz");
        UserMessage commandMessage = new UserMessage("Ayaz", "#quit");
        PrintWriter writer = mock(PrintWriter.class);
        BufferedReader reader = mock(BufferedReader.class);
        UserMessageHandler spyMessageHandler = spy(messageHandler);
        spyMessageHandler.addUserToMap(user);
        spyMessageHandler.addWriterToMap(user.getNickname(), writer);
        spyMessageHandler.addReaderToMap(user.getNickname(), reader);
        doReturn(commandMessage).doThrow(InterruptedException.class).when(spyMessageHandler).takeMessage();

        spyMessageHandler.run();

        verify(reader, times(1)).close();
    }


}