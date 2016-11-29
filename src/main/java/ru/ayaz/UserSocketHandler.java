package ru.ayaz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class UserSocketHandler implements Runnable {

    private String nickname;
    private PrintWriter writer;
    private BufferedReader reader;
    private UserMessageDistributor messageDistributor;

    UserSocketHandler(Socket socket,UserMessageDistributor messageDistributor) throws IOException {
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.messageDistributor = messageDistributor;
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    PrintWriter getWriter() {
        return writer;
    }

    BufferedReader getReader() {
        return reader;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = listenForMessage()) != null) {
                UserMessage userMessage = new UserMessage(nickname, message);
                messageDistributor.enqueueMessage(userMessage);
            }
        } catch (IOException e) {
            System.out.println(nickname + " left chat");
        }
    }

    void sendMessage(String text) {
        writer.println(text);
        writer.flush();
    }

    void closeStreams() throws IOException {
        writer.close();
        reader.close();
    }

    private String listenForMessage() throws IOException {
        return reader.readLine();
    }
}
