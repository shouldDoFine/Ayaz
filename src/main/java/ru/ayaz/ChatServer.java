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
                UserHandler userHandler = registerUserAndCreateHandler(socket);
                Thread t = new Thread(userHandler);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("User lost connection");
        } catch (RegistrationFailException e) {
            System.out.println(e.toString());
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


    private UserHandler registerUserAndCreateHandler(Socket socket) throws IOException, RegistrationFailException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            welcomeUser(writer);
            User user = new User();
            user.setNickname(reader.readLine());
            putUserAndWriterToMaps(user, writer);
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
        writeSystemMessageToThisUser("Welcome to Chat" + "Please enter your name:\n", writer);
    }

    private void putUserAndWriterToMaps(User user, PrintWriter writer) {
        usersMap.put(user.getNickname(), user);
        outputWriterMap.put(user.getNickname(), writer);
    }

    private void writeSystemMessageToThisUser(String message, PrintWriter writer) {
        writer.println(message);
        writer.flush();
    }
}
