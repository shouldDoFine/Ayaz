package ru.ayaz;


import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Chat {

    private ServerSocket serverSocket;
    private ChatRoom room;

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
                UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);
                room.registerAtChatRoom(userSocketHandler.getUser(), userSocketHandler);
                new Thread(userSocketHandler).start();
            }
        } catch (IOException e) {
            System.out.println("User lost connection");
        } catch (InvalidNicknameException e) {
            System.out.println(e.toString());
        }
    }


    private void startUserMessageDistributor() {
        BlockingQueue<UserMessage> messageQueue = new ArrayBlockingQueue(500, true);
        this.room = new ChatRoom(messageQueue);
        new Thread(room).start();
    }
}
