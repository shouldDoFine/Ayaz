package ru.ayaz;

import java.util.Map;

public class MessageBroadcaster {

    private Map<String, User> userMap;
    private Map<String, UserSocketHandler> userSocketHandlerMap;

    MessageBroadcaster(Map<String, User> userMap, Map<String, UserSocketHandler> userSocketHandlerMap) {
        this.userMap = userMap;
        this.userSocketHandlerMap = userSocketHandlerMap;
    }

    void sendMessageToEveryone(UserMessage textMessage) {
        String senderNickname = textMessage.getSenderName();
        String text = textMessage.getText();

        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            String receiverNickname = entry.getKey();
            User user = entry.getValue();

            if (user.isItMe(senderNickname)) {
                continue;
            }

            UserMessage message = new UserMessage(receiverNickname, senderNickname + ": " + text);
            sendMessageToOnlyOne(message);
        }
    }


    void sendMessageToOnlyOne(UserMessage message) {
        UserSocketHandler userSocketHandler = userSocketHandlerMap.get(message.getSenderName());
        userSocketHandler.sendMessage(message);
    }

}
