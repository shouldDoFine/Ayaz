package ru.ayaz;

public class UserMessage {
    private String senderName;
    private String text;

    public UserMessage(String senderName, String message) {
        this.senderName = senderName;
        this.text = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        UserMessage userMessage = (UserMessage) o;
        if(this.senderName.equals(userMessage.getSenderName())) {
            if(this.text.equals(userMessage.getText())){
                return true;
            }
        }
        return false;
    }
}
