package ru.ayaz;

public class MessageParser {

    static boolean isCommand(String message) {
        return message.trim().startsWith("#");
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
