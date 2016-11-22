package ru.ayaz;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Chat {

    private ServerSocket serverSocket;
    private UserMessageHandler messageHandler;

    public Chat() {
        try {
            this.serverSocket = new ServerSocket(4400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        startUserMessageHandler();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                UserHandler userHandler = registerUserAndCreateHandler(socket);
                userHandler.setMessageHandler(messageHandler);
                new Thread(userHandler).start();
            }
        } catch (IOException e) {
            System.out.println("User lost connection");
        } catch (RegistrationFailException e) {
            System.out.println(e.toString());
        }
    }


    private void startUserMessageHandler() {
        messageHandler = new UserMessageHandler(500);
        new Thread(messageHandler).start();
    }


    private UserHandler registerUserAndCreateHandler(Socket socket) throws IOException, RegistrationFailException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            welcomeUser(writer);
            User user = new User();
            user.setNickname(reader.readLine());
            messageHandler.addToUsersMap(user);
            messageHandler.addToWritersMap(user.getNickname(), writer);
            messageHandler.addToReadersMap(user.getNickname(), reader);
            UserHandler userHandler = new UserHandler(user);
            userHandler.setReader(reader);
            return userHandler;
        } catch (InvalidNicknameException e) {
            writeSystemMessageToThisUser(e.toString(), writer);
            reader.close();
            throw new RegistrationFailException("Bad nickname");
        }
    }


    private void welcomeUser(PrintWriter writer) {
        writeSystemMessageToThisUser("Welcome to Chat. " + "Please enter your name:\n", writer);
    }


    private void writeSystemMessageToThisUser(String message, PrintWriter writer) {
        writer.println(message);
        writer.flush();
    }



}
