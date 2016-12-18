package ru.ayaz;

public class ChatRoomThread implements Runnable {

    private ChatRoom room;

    ChatRoomThread(ChatRoom room) {
        this.room = room;
    }

    @Override
    public void run() {
        try {
            while (true) {
                room.sendNextMessageToEveryone();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
