package ru.ayaz;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class ChatServer {

    private ServerSocket serverSocket;
    private Map<String, User> usersMap;
    private Map<String, PrintWriter> outputWriterMap;


    public ChatServer() {
        try {
            this.serverSocket = new ServerSocket(4400);
            this.usersMap = new TreeMap<String, User>();
            this.outputWriterMap = new TreeMap<String, PrintWriter>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendMessageToEveryone(String message, String sender) {

        System.out.println(message);

        for (Map.Entry<String, User> entry : usersMap.entrySet()) {
            String key = entry.getKey();
            User value = entry.getValue();
            if(!key.equals(sender)) {
                if (!value.isInIgnoredSet(sender)) {
                    PrintWriter pw = outputWriterMap.get(key);
                    pw.println(message);
                    pw.flush();
                }
            }
        }

    }


    public void startChat() {

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                Thread t = new Thread(new UserHandler(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ChatServer chat = new ChatServer();
        chat.startChat();
    }


    public class UserHandler implements Runnable {

        private User user;
        private PrintWriter writer;
        private Socket socket;
        private BufferedReader reader;


        public UserHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
                writer = new PrintWriter(socket.getOutputStream());

                user = new User();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void run() {

            String message;

            welcomeNewUser();

            try {

                user.setNickname(listenForMessage());
                usersMap.put(user.getNickname(), user);
                outputWriterMap.put(user.getNickname(), writer);

                sendMessageToEveryone(user.getNickname() + " connected to chat", user.getNickname());

                while ((message = listenForMessage()) != null) {
                    sendMessageToEveryone(user.getNickname() + ": " + message, user.getNickname());
                }

                sendMessageToEveryone(user.getNickname() + " left chat", user.getNickname());

                Thread.currentThread().interrupt();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }


        private void welcomeNewUser() {
            writer.println("Welcome to Chat");
            writer.println("Please enter your name:\n");
            writer.flush();
        }


        private String listenForMessage() throws IOException {
            return reader.readLine();
        }

    }

}
