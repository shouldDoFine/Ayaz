package ru.ayaz;

import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;

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
    private CommandExecutor executor;

    UserSocketHandler(Socket socket, ChatRoom messageDistributor) throws IOException, InvalidNicknameException {
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.room = messageDistributor;
        this.user = registerUser();
        this.executor = new CommandExecutor(user, this);
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = listenForMessage()) != null) {
                UserMessage userMessage = new UserMessage(user.getNickname(), message);
                if (isCommand(userMessage.getText())) {
                    executor.executeCommand(userMessage);
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

    void closeStreams() throws IOException {
        writer.close();
        reader.close();
    }

    private void askNickname() {
        sendMessage(new UserMessage("SYSTEM", "Enter your nickname: \n"));
    }

    private User registerUser() throws IOException, InvalidNicknameException {
        askNickname();
        return new User(listenForMessage());
    }

    private String listenForMessage() throws IOException {
        return reader.readLine();
    }

    static boolean isCommand(String message) {
        return message.trim().startsWith("#");
    }
}
