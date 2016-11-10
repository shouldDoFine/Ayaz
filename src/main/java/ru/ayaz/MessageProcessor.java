package ru.ayaz;

import java.util.ArrayList;

public class MessageProcessor {

    public boolean isCommand(String message){
        message = message.trim();
        return message.startsWith("#");
    }

    public String getCommand(String message){
        message = message.trim();
        if(message.contains(" ")){
            return message.substring(0, message.indexOf(" "));
        }else{
            return message;
        }
    }

    public String getFirstArgument(String message){
        message = message.trim();
        message = message.substring(message.indexOf(" ") + 1);
        if(message.contains(" ")){
            message = message.substring(0, message.indexOf(" "));
        }
        return message;
    }

}
