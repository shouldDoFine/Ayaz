package ru.ayaz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UserMessageHandler implements Runnable {

    private Map<String, User> usersMap;
    private Map<String, PrintWriter> outputWriterMap;
    private Map<String, BufferedReader> inputReaderMap;
    private BlockingQueue<UserMessage> messageQueue;
    private MessageParser parser;

    public UserMessageHandler(int queueSize) {
        usersMap = new HashMap<String, User>();
        outputWriterMap = new HashMap<String, PrintWriter>();
        inputReaderMap = new HashMap<String, BufferedReader>();
        messageQueue = new ArrayBlockingQueue(queueSize, true);
        parser = new MessageParser();
    }

    public void enqueueMessage(UserMessage message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean queueContainsMessage(UserMessage message) {
        return messageQueue.contains(message);
    }

    public UserMessage takeMessage() throws InterruptedException {
        return messageQueue.take();
    }

    public void addToUsersMap(User user) {
        usersMap.put(user.getNickname(), user);
    }

    public void addToWritersMap(String nickname, PrintWriter writer) {
        outputWriterMap.put(nickname, writer);
    }

    public void addToReadersMap(String nickname, BufferedReader reader) {
        inputReaderMap.put(nickname, reader);
    }


    @Override
    public void run() {
        try {
            while (true) {
                UserMessage message = takeMessage();
                if (parser.isCommand(message.getText())) {
                    executeCommand(message);
                } else {
                    sendMessageToEveryone(message);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void executeCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        switch (parser.getCommand(text)) {
            case "#quit":
                executeQuitCommand(commandMessage);
                break;
            case "#ignore":
                executeIgnoreCommand(commandMessage);
                break;
            default:
                UserMessage message = new UserMessage(nickname, "Unknown command");
                sendMessageToOnlyOne(message);
                break;
        }
    }


    private void executeQuitCommand(UserMessage commandMessage) {
        closeWriterAndReader(commandMessage.getSenderName());
    }


    private void executeIgnoreCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        User user = usersMap.get(nickname);
        try {
            user.ignoreUser(parser.getFirstArgument(text));
        } catch (InvalidUserCommandException e) {
            UserMessage message = new UserMessage(nickname, "Invalid argument");
            sendMessageToOnlyOne(message);
        }
    }


    private void closeWriterAndReader(String nickname) {
        try {
            outputWriterMap.get(nickname).close();
            inputReaderMap.get(nickname).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendMessageToEveryone(UserMessage textMessage) {
        String senderNickname = textMessage.getSenderName();
        String text = textMessage.getText();

        for (Map.Entry<String, User> entry : usersMap.entrySet()) {
            String receiverNickname = entry.getKey();
            User user = entry.getValue();

            if (user.isItMe(senderNickname)) {
                continue;
            }

            UserMessage message = new UserMessage(receiverNickname, senderNickname + ": " + text);
            sendMessageToOnlyOne(message);
        }
    }


    private void sendMessageToOnlyOne(UserMessage message) {
        PrintWriter writer = outputWriterMap.get(message.getSenderName());
        writer.println(message.getText());
        writer.flush();
    }
}
