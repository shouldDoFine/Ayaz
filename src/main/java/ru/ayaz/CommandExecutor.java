package ru.ayaz;

import java.io.IOException;
import java.util.Map;

import static ru.ayaz.MessageParser.getCommand;
import static ru.ayaz.MessageParser.getFirstArgument;

public class CommandExecutor {

    private MessageBroadcaster broadcaster;
    private Map<String, User> userMap;
    private Map<String, UserSocketHandler> userSocketHandlerMap;

    CommandExecutor(Map<String, User> userMap, Map<String, UserSocketHandler> userSocketHandlerMap) {
        this.userMap = userMap;
        this.userSocketHandlerMap = userSocketHandlerMap;
        this.broadcaster = new MessageBroadcaster(userMap, userSocketHandlerMap);
    }

    void executeCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        switch (getCommand(text)) {
            case "#quit":
                executeQuitCommand(commandMessage);
                break;
            case "#ignore":
                executeIgnoreCommand(commandMessage);
                break;
            default:
                UserMessage message = new UserMessage(nickname, "Unknown command");
                broadcaster.sendMessageToOnlyOne(message);
                break;
        }
    }


    void executeQuitCommand(UserMessage commandMessage) {
        UserSocketHandler userSocketHandler = userSocketHandlerMap.get(commandMessage.getSenderName());
        try {
            userSocketHandler.closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void executeIgnoreCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        User user = userMap.get(nickname);
        try {
            user.ignoreUser(getFirstArgument(text));
        } catch (InvalidUserCommandException e) {
            UserMessage message = new UserMessage(nickname, "Invalid argument");
            broadcaster.sendMessageToOnlyOne(message);
        }
    }
}
