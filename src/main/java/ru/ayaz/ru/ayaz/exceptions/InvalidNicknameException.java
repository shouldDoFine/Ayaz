package ru.ayaz.ru.ayaz.exceptions;

public class InvalidNicknameException extends Exception {

    private String invalidNickname;

    public InvalidNicknameException(String invalidNickname) {
        this.invalidNickname = invalidNickname;
    }

    @Override
    public String toString() {
        return "Bad nickname " + invalidNickname + "." + "Digit first and spaces are not allowed.";
    }

}
