package ru.ayaz;

public class UserMessage {

    private String senderName;
    private String text;

    UserMessage(String senderName, String message) {
        this.senderName = senderName;
        this.text = message;
    }

    String getSenderName() {
        return senderName;
    }

    String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        UserMessage userMessage = (UserMessage) o;
        if (senderName.equals(userMessage.getSenderName())) {
            if (text.equals(userMessage.getText())) {
                return true;
            }
        }
        return false;
    }
}
