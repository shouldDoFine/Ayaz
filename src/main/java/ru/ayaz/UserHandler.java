package ru.ayaz;

import java.io.BufferedReader;
import java.io.IOException;


public class UserHandler implements Runnable {

    private User user;
    private BufferedReader reader;
    private UserMessageHandler messageHandler;


    public UserHandler(User user) {
        this.user = user;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void setMessageHandler(UserMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = listenForMessage()) != null) {
                UserMessage userMessage = new UserMessage(user.getNickname(), message);
                messageHandler.enqueueMessage(userMessage);
            }
        } catch (IOException e) {
            System.out.println(user.getNickname() + " left chat");
        }
    }

    private String listenForMessage() throws IOException {
        return reader.readLine();
    }

}
