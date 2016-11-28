package ru.ayaz;

import java.io.IOException;
import java.util.Map;

import static ru.ayaz.MessageParser.getCommand;
import static ru.ayaz.MessageParser.getFirstArgument;

public class CommandExecutor {

    private MessageBroadcaster broadcaster;
    private Map<String, User> usersMap;
    private Map<String, UserSocketHandler> userHandlerMap;

    CommandExecutor(Map<String, User> usersMap, Map<String, UserSocketHandler> userHandlerMap) {
        this.usersMap = usersMap;
        this.userHandlerMap = userHandlerMap;
        broadcaster = new MessageBroadcaster(usersMap, userHandlerMap);
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
        UserSocketHandler userHandler = userHandlerMap.get(commandMessage.getSenderName());
        try {
            userHandler.closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void executeIgnoreCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        User user = usersMap.get(nickname);
        try {
            user.ignoreUser(getFirstArgument(text));
        } catch (InvalidUserCommandException e) {
            UserMessage message = new UserMessage(nickname, "Invalid argument");
            broadcaster.sendMessageToOnlyOne(message);
        }
    }
}
