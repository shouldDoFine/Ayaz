package ru.ayaz;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class ChatRoomTest {

    private ChatRoom room;

    @Before
    public final void before() {
        this.room = new ChatRoom();
    }

    @Test
    public void shouldBeInChatRoomWhenUserHasBeenRegisteredBefore() throws Exception {
        User user = new User("Ayaz");
        UserSocketHandler userSocketHandler = mock(UserSocketHandler.class);
        when(userSocketHandler.getUser()).thenReturn(user);

        room.registerSocketHandler(userSocketHandler);

        assertTrue(room.isUserInChatRoom("Ayaz"));
    }

    @Test
    public void shouldSendMessageToReceiverWhenMessageTaken() throws Exception {
        User receiverUser = new User("Alexandr");
        UserSocketHandler receiverSocketHandler = mock(UserSocketHandler.class);
        when(receiverSocketHandler.getUser()).thenReturn(receiverUser);
        room.registerSocketHandler(receiverSocketHandler);
        UserMessage message = new UserMessage("Ayaz", "Hello, Alexandr!");
        room.enqueueMessage(message);

        room.sendNextMessageToEveryone();

        verify(receiverSocketHandler).sendMessage(new UserMessage("Ayaz", "Ayaz: Hello, Alexandr!"));
    }

    @Test
    public void shouldNotSendToYourselfWhenYoursMessageTaken() throws Exception {
        User senderUser = new User("Ayaz");
        String senderNickname = "Ayaz";
        String senderMessage = "Is anyone here?";
        UserSocketHandler senderSocketHandler = mock(UserSocketHandler.class);
        when(senderSocketHandler.getUser()).thenReturn(senderUser);
        room.registerSocketHandler(senderSocketHandler);
        UserMessage message = new UserMessage(senderNickname, senderMessage);
        room.enqueueMessage(message);

        room.sendNextMessageToEveryone();

        verify(senderSocketHandler, never()).sendMessage(new UserMessage(senderNickname, senderNickname + ": " + senderMessage));
    }
}