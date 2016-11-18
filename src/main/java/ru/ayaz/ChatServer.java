package ru.ayaz;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

    private ServerSocket serverSocket;
    private Map<String, User> usersMap;
    private Map<String, PrintWriter> outputWriterMap;


    public ChatServer() {
        try {
            this.serverSocket = new ServerSocket(4400);
            this.usersMap = new HashMap<String, User>();
            this.outputWriterMap = new HashMap<String, PrintWriter>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                UserHandler userHandler = new UserHandler(socket);
                userHandler.setChatReference(this);
                Thread t = new Thread(userHandler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessageToEveryone(String message, String sender) {

        System.out.println(message);

        for (Map.Entry<String, User> entry : usersMap.entrySet()) {
            String nickname = entry.getKey();
            User user = entry.getValue();

            if (user.isItMe(sender)) {
                continue;
            }

            if (!user.isIgnored(sender)) {
                PrintWriter pw = outputWriterMap.get(nickname);
                pw.println(message);
                pw.flush();
            }

        }

    }


    public void registerUser(User user, PrintWriter writer) {
        usersMap.put(user.getNickname(), user);
        outputWriterMap.put(user.getNickname(), writer);
    }

}
