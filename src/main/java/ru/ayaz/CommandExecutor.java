package ru.ayaz;

import ru.ayaz.ru.ayaz.exceptions.InvalidUserCommandException;

import java.io.IOException;

public class CommandExecutor {

    private User user;
    private UserSocketHandler userSocketHandler;


    CommandExecutor(User user, UserSocketHandler userSocketHandler) {
        this.user = user;
        this.userSocketHandler = userSocketHandler;
    }

    void executeCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        switch (getCommand(text)) {
            case "#quit":
                executeQuitCommand();
                break;
            case "#ignore":
                executeIgnoreCommand(commandMessage);
                break;
            default:
                UserMessage message = new UserMessage(nickname, "Unknown command");
                userSocketHandler.sendMessage(message);
                break;
        }
    }

    private void executeQuitCommand() {
        try {
            userSocketHandler.closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeIgnoreCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        try {
            user.ignoreUser(getFirstArgument(text));
        } catch (InvalidUserCommandException e) {
            UserMessage message = new UserMessage(nickname, "Invalid command");
            userSocketHandler.sendMessage(message);
        }
    }

    static String getCommand(String message) {
        String trimmedMessage = message.trim();
        if (trimmedMessage.contains(" ")) {
            return trimmedMessage.substring(0, trimmedMessage.indexOf(" "));
        } else {
            return trimmedMessage;
        }
    }

    static String getFirstArgument(String message) {
        String[] arguments = message.trim().split("\\s+");
        return arguments[1];
    }
}
