package ru.ayaz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class UserHandler implements Runnable {

    private User user;
    private ChatServer chat;
    private MessageProcessor processor;
    private PrintWriter writer;
    private BufferedReader reader;


    public UserHandler(Socket socket) {
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
            this.user = new User();
            this.processor = new MessageProcessor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setChatReference(ChatServer chat) {
        this.chat = chat;
    }


    public void run() {

        welcomeUser();

        try {

            user.setNickname(listenForMessage());
            chat.registerUser(user, writer);
            startListeningToUser();

        } catch (InvalidNicknameException e) {
            writeSystemMessageToThisUser(e.toString());
            try {
                closeWriterAndReader();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void welcomeUser() {
        writeSystemMessageToThisUser("Welcome to Chat" + "Please enter your name:\n");
    }


    private void writeSystemMessageToThisUser(String message) {
        writer.println(message);
        writer.flush();
    }


    private String listenForMessage() throws IOException {
        return reader.readLine();
    }


    private void sayEveryoneThatUserHasConnected(String connectedUser) {
        chat.sendMessageToEveryone(connectedUser + " connected to chat", connectedUser);
    }


    private void sayEveryoneThatUserHasLeft(String leftUser) {
        chat.sendMessageToEveryone(leftUser + " left chat", leftUser);
    }


    private void executeCommand(String commandMessage) throws IOException {
        switch (processor.getCommand(commandMessage)) {
            case "#quit":
                closeWriterAndReader();
                break;
            case "#ignore":
                try {
                    user.ignoreUser(processor.getFirstArgument(commandMessage));
                } catch (InvalidUserCommandException e) {
                    writeSystemMessageToThisUser(e.toString());
                }
                break;
            default:
                writeSystemMessageToThisUser("Unknown command");
                break;
        }
    }


    private void startListeningToUser() {
        String message;
        String sender = user.getNickname();

        sayEveryoneThatUserHasConnected(sender);

        try {
            while ((message = listenForMessage()) != null) {
                if (!processor.isCommand(message)) {
                    chat.sendMessageToEveryone(sender + ": " + message, sender);
                } else {
                    executeCommand(message);
                }
            }
        } catch (IOException e) {
            sayEveryoneThatUserHasLeft(sender);
        }
    }


    private void closeWriterAndReader() throws IOException {
        writer.close();
        reader.close();
    }

}
