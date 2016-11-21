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

    public void addUserToMap(User user) {
        usersMap.put(user.getNickname(), user);
    }

    public void addWriterToMap(String nickname, PrintWriter writer) {
        outputWriterMap.put(nickname, writer);
    }

    public void addReaderToMap(String nickname, BufferedReader reader) {
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
                PrintWriter writer = outputWriterMap.get(nickname);
                sendMessageToExactPerson("Unknown command", writer);
                break;
        }
    }


    private void executeQuitCommand(UserMessage commandMessage) {
        closeWriterAndReaderInMap(commandMessage.getSenderName());
    }


    private void executeIgnoreCommand(UserMessage commandMessage) {
        String nickname = commandMessage.getSenderName();
        String text = commandMessage.getText();

        User user = usersMap.get(nickname);
        try {
            user.ignoreUser(parser.getFirstArgument(text));
        } catch (InvalidUserCommandException e) {
            PrintWriter writer = outputWriterMap.get(nickname);
            sendMessageToExactPerson("Invalid argument", writer);
        }
    }


    private void closeWriterAndReaderInMap(String nickname) {
        try {
            outputWriterMap.get(nickname).close();
            inputReaderMap.get(nickname).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendMessageToEveryone(UserMessage message) {
        String senderNickname = message.getSenderName();
        String text = message.getText();

        for (Map.Entry<String, User> entry : usersMap.entrySet()) {
            String receiverNickname = entry.getKey();
            User user = entry.getValue();

            if (user.isItMe(senderNickname)) {
                continue;
            }

            PrintWriter writer = outputWriterMap.get(receiverNickname);
            sendMessageToExactPerson(senderNickname + ": " + text, writer);
        }
    }


    private void sendMessageToExactPerson(String text, PrintWriter writer) {
        writer.println(text);
        writer.flush();
    }
}
