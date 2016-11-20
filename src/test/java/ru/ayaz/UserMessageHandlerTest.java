package ru.ayaz;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class UserMessageHandlerTest {

    private UserMessageHandler messageHandler;

    @Before
    public final void before() {
        int queueSize = 500;
        messageHandler = new UserMessageHandler(queueSize);
    }

    @Test
    public void shouldPutMessage() throws InterruptedException {
        Message someMessage = new Message();
        someMessage.setSenderName("Ayaz");
        someMessage.setMessage("Hello everyone.");
        messageHandler.enqueueMessage(someMessage);
        assertTrue(messageHandler.containsMessage(someMessage));
    }

    @Test
    public void shouldTakeMessage() throws InterruptedException {
        Message someMessage = new Message();
        someMessage.setSenderName("Ayaz");
        someMessage.setMessage("Hello everyone.");
        messageHandler.enqueueMessage(someMessage);
        Message gottenMessage = messageHandler.takeMessage();
        assertEquals(someMessage, gottenMessage);
    }

    @Ignore
    @Test
    public void shouldRegisterUserWhenDoesntContain() throws InterruptedException{
        Message someMessage = new Message();
        someMessage.setSenderName("Ayaz");
        someMessage.setMessage("Hello everyone.");
        messageHandler.enqueueMessage(someMessage);
        Message gottenMessage = messageHandler.takeMessage();
        messageHandler.processMessage(gottenMessage);
        assertTrue(messageHandler.isRegistered(gottenMessage.getSenderName()));
    }



}