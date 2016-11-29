package ru.ayaz.ru.ayaz.exceptions;

public class RegistrationFailException extends Exception {

    private String cause;

    public RegistrationFailException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "Failed to register user. The cause is: " + this.cause;
    }
}
