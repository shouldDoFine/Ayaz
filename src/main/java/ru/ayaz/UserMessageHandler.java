package ru.ayaz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UserMessageHandler implements Runnable{
    private Map<String, User> usersMap;
    private BlockingQueue<Message> messageQueue;
    private MessageProcessor processor;

    public UserMessageHandler(int queueSize) {
        usersMap = new HashMap<String, User>();
        messageQueue = new ArrayBlockingQueue(queueSize, true);
        processor = new MessageProcessor();
    }

    public void enqueueMessage(Message message) throws InterruptedException {
        messageQueue.put(message);
    }

    public boolean containsMessage(Message message) {
        return messageQueue.contains(message);
    }

    public Message takeMessage() throws InterruptedException {
        return messageQueue.take();
    }

    public boolean isRegistered(String nickname) {
        return usersMap.containsKey(nickname);
    }

    public void processMessage(Message message) {
        String sender = message.getSenderName();
        if(!usersMap.containsKey(sender)){

        }
    }


    @Override
    public void run() {
        try {
            while(true) {
                Message message = takeMessage();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
