package ru.ayaz;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Chat {

    private ServerSocket serverSocket;
    private UserMessageDistributor messageHandler;

    Chat() {
        try {
            this.serverSocket = new ServerSocket(4400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void start() {
        startUserMessageHandler();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                UserSocketHandler userSocketHandler = new UserSocketHandler(socket, messageHandler);
                User user = registerUser(userSocketHandler.getWriter(), userSocketHandler.getReader());
                userSocketHandler.setNickname(user.getNickname());
                messageHandler.registerAtMessageDistributor(user, userSocketHandler);
                new Thread(userSocketHandler).start();
            }
        } catch (IOException e) {
            System.out.println("User lost connection");
        } catch (RegistrationFailException e) {
            System.out.println(e.toString());
        }
    }


    private void startUserMessageHandler() {
        this.messageHandler = new UserMessageDistributor();
        new Thread(messageHandler).start();
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
