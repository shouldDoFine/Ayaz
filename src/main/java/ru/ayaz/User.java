package ru.ayaz;

import java.io.PrintWriter;

public class User {
    private String nickName;
    private long id;
    private PrintWriter writer;

    public void setNickName(String nickName) {
        validateNickName(nickName);
        this.nickName = nickName;
    }

    private void validateNickName(String nickName) {
        if (nickName == null) {
            throw new IllegalArgumentException();
        }

        if (isSpacesOnly(nickName)) {
            throw new IllegalArgumentException();
        }

        if (isFirstCharDigit(nickName)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isFirstCharDigit(String nickName) {
        return Character.isDigit(nickName.charAt(0));
    }

    private boolean isSpacesOnly(String nickName) {
        return nickName.replaceAll("\\s+", "").equals("");
    }


    public String getNickName() {
        return nickName;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
