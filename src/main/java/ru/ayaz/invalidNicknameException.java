package ru.ayaz;

public class invalidNicknameException extends Exception {

    private String invalidNickname;

    public invalidNicknameException(String invalidNickname) {
        this.invalidNickname = invalidNickname;
    }

    public String getInvalidNickname() {
        return invalidNickname;
    }

}
