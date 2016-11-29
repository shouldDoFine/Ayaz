package ru.ayaz;


import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;
import ru.ayaz.ru.ayaz.exceptions.RegistrationFailException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Chat {

    private ServerSocket serverSocket;
    private UserMessageDistributor messageDistributor;

    Chat() {
        try {
            this.serverSocket = new ServerSocket(4400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void start() {
        startUserMessageDistributor();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                UserSocketHandler userSocketHandler = new UserSocketHandler(socket, messageDistributor);
                User user = registerUser(userSocketHandler.getWriter(), userSocketHandler.getReader());
                userSocketHandler.setNickname(user.getNickname());
                messageDistributor.registerAtMessageDistributor(user, userSocketHandler);
                new Thread(userSocketHandler).start();
            }
        } catch (IOException e) {
            System.out.println("User lost connection");
        } catch (RegistrationFailException e) {
            System.out.println(e.toString());
        }
    }


    private void startUserMessageDistributor() {
        HashMap<String, User> userMap = new HashMap<>();
        HashMap<String, UserSocketHandler> userSocketHandlerMap = new HashMap<>();
        BlockingQueue<UserMessage> messageQueue = new ArrayBlockingQueue(500, true);
        MessageBroadcaster broadcaster = new MessageBroadcaster(userMap, userSocketHandlerMap);
        CommandExecutor executor = new CommandExecutor(userMap, userSocketHandlerMap);
        this.messageDistributor = new UserMessageDistributor(userMap, userSocketHandlerMap, messageQueue, broadcaster, executor);
        new Thread(messageDistributor).start();
    }


    private User registerUser(PrintWriter writer, BufferedReader reader) throws IOException, RegistrationFailException {
        askNickname(writer);
        try {
            User user = new User(listenForNickname(reader));
            return user;
        } catch (InvalidNicknameException e) {
            writeMessageToWriter(e.toString(), writer);
            reader.close();
            throw new RegistrationFailException("Bad nickname");
        }
    }

    private void askNickname(PrintWriter writer) {
        writeMessageToWriter("Enter your nickname: \n", writer);
    }

    private String listenForNickname(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private void writeMessageToWriter(String message, PrintWriter writer) {
        writer.println(message);
        writer.flush();
    }
}
