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


    public void start() {

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


    private void sendMessageToEveryone(String message, String sender) {

        System.out.println(message);

        for (Map.Entry<String, User> entry : usersMap.entrySet()) {
            String nickname = entry.getKey();
            User user = entry.getValue();
            if (!nickname.equals(sender)) {
                if (!user.isInIgnoredSet(sender)) {
                    PrintWriter pw = outputWriterMap.get(nickname);
                    pw.println(message);
                    pw.flush();
                }
            }
        }

    }


    public class UserHandler implements Runnable {

        private User user;
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


        public void run() {

            String message;
            String sender;

            welcomeNewUser();

            try {

                user.setNickname(listenForMessage());
                usersMap.put(user.getNickname(), user);
                outputWriterMap.put(user.getNickname(), writer);

                sender = user.getNickname();

                sendMessageToEveryone(sender + " connected to chat", sender);

                while ((message = listenForMessage()) != null) {
                    if (!processor.isCommand(message)) {
                        sendMessageToEveryone(sender + ": " + message, sender);
                    } else {
                        executeCommand(message);
                    }
                }

                sendMessageToEveryone(sender + " left chat", sender);

                Thread.currentThread().interrupt();

            } catch (invalidNicknameException e) {
                writer.println("Bad nickname " + e.getInvalidNickname() + ".");
                writer.println("Digit first and spaces are not allowed.");
                writer.flush();
                writer.close();
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                sendMessageToEveryone(user.getNickname() + " left chat", user.getNickname());
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


        private void executeCommand(String message) throws IOException {
            switch (processor.getCommand(message)) {
                case "#quit":
                    writer.close();
                    reader.close();
                    break;
                case "#ignore":
                    user.ignoreUser(processor.getFirstArgument(message));
                    break;
                default:
                    writer.println("Unknown command");
                    writer.flush();
                    break;
            }
        }

    }

}
