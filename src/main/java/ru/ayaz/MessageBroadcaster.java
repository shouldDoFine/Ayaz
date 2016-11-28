package ru.ayaz;

import java.util.Map;

public class MessageBroadcaster {

    private Map<String, User> usersMap;
    private Map<String, UserSocketHandler> userHandlerMap;

    MessageBroadcaster(Map<String, User> usersMap, Map<String, UserSocketHandler> userHandlerMap) {
        this.usersMap = usersMap;
        this.userHandlerMap = userHandlerMap;
    }

    void sendMessageToEveryone(UserMessage textMessage) {
        String senderNickname = textMessage.getSenderName();
        String text = textMessage.getText();

        for (Map.Entry<String, User> entry : usersMap.entrySet()) {
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
        UserSocketHandler userHandler = userHandlerMap.get(message.getSenderName());
        userHandler.sendMessage(message);
    }

}
