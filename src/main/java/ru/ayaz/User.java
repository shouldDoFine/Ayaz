package ru.ayaz;

import java.io.PrintWriter;
import java.io.Writer;
import java.security.Principal;

/**
 * Created by ayaz on 11/5/16.
 */
public class User {
    private String nickName;
    private long id;
    private PrintWriter writer;

    public void setNickName(String nickName){

        if((nickName == null)||(nickName.equals(""))){
            throw new IllegalArgumentException();
        }

        if(nickName.replaceAll("\\s+", "").equals("")){
           throw new IllegalArgumentException();
        }

        if(Character.isDigit(nickName.charAt(0))){
            throw new IllegalArgumentException();
        }


        this.nickName = nickName;

    }


    public String getNickName() {
        return nickName;
    }

    public void setWriter(PrintWriter writer){
        this.writer = writer;
    }

    public PrintWriter getWriter(){
        return this.writer;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
