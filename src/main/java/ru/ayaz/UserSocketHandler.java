package ru.ayaz;

import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;
import ru.ayaz.ru.ayaz.exceptions.InvalidUserCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class UserSocketHandler implements Runnable {

    private ChatRoom room;
    private PrintWriter writer;
    private BufferedReader reader;
    private User user;

    UserSocketHandler(Socket socket, ChatRoom room) throws IOException, InvalidNicknameException {
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.room = room;
        this.user = registerUser();
    }

    User getUser() {
        return user;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = listenForMessage()) != null) {
                UserMessage userMessage = new UserMessage(user.getNickname(), message);
                if (isCommand(userMessage.getText())) {
                    executeCommand(userMessage);
                } else {
                    room.enqueueMessage(userMessage);
                }
            }
        } catch (IOException e) {
            System.out.println(user.getNickname() + " left chat");
        }
    }

    void sendMessage(UserMessage message) {
        writer.println(message.getText());
        writer.flush();
    }

    private void executeCommand(UserMessage commandMessage) {
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
                sendMessage(message);
                break;
        }
    }

    private void executeQuitCommand() {
        try {
            closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeStreams() throws IOException {
        writer.close();
        reader.close();
    }

    private void executeIgnoreCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        try {
            user.ignoreUser(getFirstArgument(text));
            UserMessage message = new UserMessage(nickname, "User successfully ignored");
            sendMessage(message);
        } catch (InvalidUserCommandException e) {
            UserMessage message = new UserMessage(nickname, "Invalid command");
            sendMessage(message);
        }
    }


    private User registerUser() throws IOException, InvalidNicknameException {
        askNickname();
        return new User(listenForMessage());
    }

    private void askNickname() {
        sendMessage(new UserMessage("SYSTEM", "Enter your nickname: \n"));
    }

    private String listenForMessage() throws IOException {
        return reader.readLine();
    }

    private String getCommand(String message) {
        String trimmedMessage = message.trim();
        if (trimmedMessage.contains(" ")) {
            return trimmedMessage.substring(0, trimmedMessage.indexOf(" "));
        } else {
            return trimmedMessage;
        }
    }

    private String getFirstArgument(String message) {
        String[] arguments = message.trim().split("\\s+");
        return arguments[1];
    }

    private boolean isCommand(String message) {
        return message.trim().startsWith("#");
    }
}
