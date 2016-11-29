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
            User receiverUser = entry.getValue();

            if (receiverUser.isItMe(senderNickname)) {
                continue;
            }

            if (!receiverUser.isIgnored(senderNickname)) {
                sendTextToOnlyOne(senderNickname + ": " + text, receiverNickname);
            }
        }
    }

    void sendTextToOnlyOne(String text, String receiverNickname) {
        UserSocketHandler userSocketHandler = userSocketHandlerMap.get(receiverNickname);
        userSocketHandler.sendMessage(text);
    }
}
