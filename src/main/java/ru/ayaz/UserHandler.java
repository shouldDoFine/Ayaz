package ru.ayaz;

import java.io.BufferedReader;
import java.io.IOException;


public class UserHandler implements Runnable {

    private User user;
    private BufferedReader reader;


    public UserHandler(User user) {
            this.user = user;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void run() {
        //startListeningToUser();
    }

    private String listenForMessage() throws IOException {
        return reader.readLine();
    }


    /*
    private void sayEveryoneThatUserHasConnected(String connectedUser) {
        chat.sendMessageToEveryone(connectedUser + " connected to chat", connectedUser);
    }


    private void sayEveryoneThatUserHasLeft(String leftUser) {
        chat.sendMessageToEveryone(leftUser + " left chat", leftUser);
    }


    private void executeCommand(String commandMessage) throws IOException {
        switch (processor.getCommand(commandMessage)) {
            case "#quit":
                closeReader();
                break;
            case "#ignore":
                try {
                    user.ignoreUser(processor.getFirstArgument(commandMessage));
                } catch (InvalidUserCommandException e) {
                    writeSystemMessageToThisUser(e.toString());
                }
                break;
            default:
                writeSystemMessageToThisUser("Unknown command");
                break;
        }
    }


    private void startListeningToUser() {
        String message;
        String sender = user.getNickname();

        sayEveryoneThatUserHasConnected(sender);

        try {
            while ((message = listenForMessage()) != null) {
                if (!processor.isCommand(message)) {
                    chat.sendMessageToEveryone(sender + ": " + message, sender);
                } else {
                    executeCommand(message);
                }
            }
        } catch (IOException e) {
            sayEveryoneThatUserHasLeft(sender);
        }
    }
    */


    private void closeReader() throws IOException {
        reader.close();
    }

}
