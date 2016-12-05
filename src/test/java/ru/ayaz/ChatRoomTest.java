package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class ChatRoomTest {

    private ChatRoom room;

    @Before
    public final void before() {
        BlockingQueue<UserMessage> messageQueue = new ArrayBlockingQueue(500, true);
        this.room = spy(new ChatRoom(messageQueue));
    }

    @Test
    public void shouldBeAbleToEnqueueMessage() throws InterruptedException {
        UserMessage someMessage = new UserMessage("Ayaz", "Hello");

        room.enqueueMessage(someMessage);

        assertTrue(room.queueContainsMessage(someMessage));
    }


    @Test
    public void shouldBeAbleToGetEnqueuedMessage() throws InterruptedException {
        UserMessage enqueuedMessage = new UserMessage("Ayaz", "Hello");
        room.enqueueMessage(enqueuedMessage);

        UserMessage gottenMessage = room.takeMessage();

        assertEquals(enqueuedMessage, gottenMessage);
    }

    @Test
    public void shouldBeInChatRoomWhenUserHasBeenRegisteredBefore() {
        User user = mock(User.class);
        when(user.getNickname()).thenReturn("Ayaz");
        room.registerAtChatRoom(user, mock(UserSocketHandler.class));

        assertTrue(room.isUserInChatRoom("Ayaz"));
    }

    @Test
    public void shouldSendMessageToReceiverWhenMessageTaken() throws Exception {
        User receiverUser = mock(User.class);
        when(receiverUser.getNickname()).thenReturn("Alexandr");
        UserSocketHandler receiverSocketHandler = mock(UserSocketHandler.class);
        room.registerAtChatRoom(receiverUser, receiverSocketHandler);
        UserMessage message = new UserMessage("Ayaz", "Hello, Alexandr!");
        doReturn(message).doThrow(InterruptedException.class).when(room).takeMessage();

        room.run();

        verify(receiverSocketHandler).sendMessage(new UserMessage("Ayaz", "Ayaz: Hello, Alexandr!"));
    }

    @Test
    public void shouldNotSendToYourselfWhenYoursMessageTaken() throws Exception {
        User senderUser = mock(User.class);
        when(senderUser.getNickname()).thenReturn("Ayaz");
        when(senderUser.isItMe("Ayaz")).thenReturn(true);
        UserSocketHandler senderSocketHandler = mock(UserSocketHandler.class);
        room.registerAtChatRoom(senderUser, senderSocketHandler);
        UserMessage message = new UserMessage("Ayaz", "Is anyone here?");
        doReturn(message).doThrow(InterruptedException.class).when(room).takeMessage();

        room.run();

        verify(senderSocketHandler, never()).sendMessage(new UserMessage("Ayaz", "Ayaz: Is anyone here?"));
    }
}