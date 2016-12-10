package ru.ayaz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ChatRoom implements Runnable {

    private Map<String, User> userMap;
    private Map<String, UserSocketHandler> userSocketHandlerMap;
    private BlockingQueue<UserMessage> messageQueue;

    ChatRoom(BlockingQueue<UserMessage> queue) {
        this.userMap = new HashMap<>();
        this.userSocketHandlerMap = new HashMap<>();
        this.messageQueue = queue;
    }

    void enqueueMessage(UserMessage message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    UserMessage takeMessage() throws InterruptedException {
        return messageQueue.take();
    }

    void registerSocketHandler(UserSocketHandler userSocketHandler) {
        User user = userSocketHandler.getUser();
        userMap.put(user.getNickname(), user);
        userSocketHandlerMap.put(user.getNickname(), userSocketHandler);
    }

    boolean isUserInChatRoom(String nickname) {
        return userMap.containsKey(nickname);
    }

    @Override
    public void run() {
        try {
            while (true) {
                sendMessageToEveryone(takeMessage());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToEveryone(UserMessage textMessage) {
        String senderNickname = textMessage.getSenderName();
        String text = textMessage.getText();

        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            String receiverNickname = entry.getKey();
            User user = entry.getValue();

            if (user.isItMe(senderNickname)) {
                continue;
            }

            UserMessage message = new UserMessage(senderNickname, senderNickname + ": " + text);
            sendMessageToOnlyOne(message, receiverNickname);
        }
    }

    private void sendMessageToOnlyOne(UserMessage message, String receiverNickname) {
        UserSocketHandler userSocketHandler = userSocketHandlerMap.get(receiverNickname);
        userSocketHandler.sendMessage(message);
    }
}
