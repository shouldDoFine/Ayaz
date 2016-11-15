package ru.ayaz;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            welcomeUser();

            try {

                user.setNickname(listenForMessage());
                addUser();
                addWriter();

                sender = user.getNickname();

                sayEveryoneUserConnected(sender);

                while ((message = listenForMessage()) != null) {
                    if (!processor.isCommand(message)) {
                        sendMessageToEveryone(sender + ": " + message, sender);
                    } else {
                        executeCommand(message);
                    }
                }

                sayEveryoneUserLeft(sender);

                Thread.currentThread().interrupt();

            } catch (InvalidNicknameException e) {
                writer.println(e.toString());
                writer.flush();
                writer.close();
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                sayEveryoneUserLeft(user.getNickname());
            }
        }


        private void welcomeUser() {
            writer.println("Welcome to Chat");
            writer.println("Please enter your name:\n");
            writer.flush();
        }


        private void addUser() {
            usersMap.put(user.getNickname(), user);
        }


        private void addWriter() {
            outputWriterMap.put(user.getNickname(), writer);
        }


        private String listenForMessage() throws IOException {
            return reader.readLine();
        }


        private void sayEveryoneUserConnected(String connectedUser) {
            sendMessageToEveryone(connectedUser + " connected to chat", connectedUser);
        }

        private void sayEveryoneUserLeft(String leftUser) {
            sendMessageToEveryone(leftUser + " left chat", leftUser);
        }

        private void executeCommand(String message) throws IOException {
            switch (processor.getCommand(message)) {
                case "#quit":
                    writer.close();
                    reader.close();
                    break;
                case "#ignore":
                    try {
                        user.ignoreUser(processor.getFirstArgument(message));
                    } catch (InvalidUserCommandException e) {
                        writer.println(e.toString());
                        writer.flush();
                    }
                    break;
                default:
                    writer.println("Unknown command");
                    writer.flush();
                    break;
            }
        }

    }

}
