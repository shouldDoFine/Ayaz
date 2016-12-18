package ru.ayaz;


import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Chat {

    private ServerSocket serverSocket;
    private ChatRoom room;


    Chat() {
        try {
            this.serverSocket = new ServerSocket(4400);
            createRoom();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void start() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                UserSocketHandler userSocketHandler = new UserSocketHandler(socket, room);
                room.registerSocketHandler(userSocketHandler);
                new Thread(userSocketHandler).start();
            }
        } catch (IOException e) {
            System.out.println("User lost connection");
        } catch (InvalidNicknameException e) {
            System.out.println(e.toString());
        }
    }

    private void createRoom() {
        this.room = new ChatRoom();
        ChatRoomThread roomThread = new ChatRoomThread(room);
        new Thread(roomThread).start();
    }
}
