package ru.ayaz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static ru.ayaz.MessageParser.isCommand;

public class UserMessageDistributor implements Runnable {

    private Map<String, User> userMap;
    private Map<String, UserSocketHandler> userSocketHandlerMap;
    private BlockingQueue<UserMessage> messageQueue;
    private MessageBroadcaster broadcaster;
    private CommandExecutor executor;

    UserMessageDistributor() {
        userMap = new HashMap<>();
        userSocketHandlerMap = new HashMap<>();
        messageQueue = new ArrayBlockingQueue(500, true);
        broadcaster = new MessageBroadcaster(userMap, userSocketHandlerMap);
        executor = new CommandExecutor(userMap, userSocketHandlerMap);
    }

    void enqueueMessage(UserMessage message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean queueContainsMessage(UserMessage message) {
        return messageQueue.contains(message);
    }

    UserMessage takeMessage() throws InterruptedException {
        return messageQueue.take();
    }

    void registerAtMessageDistributor(User user, UserSocketHandler userSocketHandler) {
        userMap.put(user.getNickname(), user);
        userSocketHandlerMap.put(user.getNickname(), userSocketHandler);
    }

    @Override
    public void run() {
        try {
            while (true) {
                UserMessage message = takeMessage();
                if (isCommand(message.getText())) {
                    executor.executeCommand(message);
                } else {
                    broadcaster.sendMessageToEveryone(message);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
